package com.hnv99.forum.core.sensitive;

import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllowSystem;
import com.github.houbb.sensitive.word.support.deny.WordDenySystem;
import com.hnv99.forum.core.autoconf.DynamicConfigContainer;
import com.hnv99.forum.core.cache.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service class for sensitive word filtering.
 */
@Slf4j
@Service
public class SensitiveService {
    /**
     * Prefix for sensitive word hit count statistics.
     */
    private static final String SENSITIVE_WORD_CNT_PREFIX = "sensitive_word";
    private volatile SensitiveWordBs sensitiveWordBs;
    @Autowired
    private SensitiveProperty sensitiveConfig;
    @Autowired
    private DynamicConfigContainer dynamicConfigContainer;

    @PostConstruct
    public void refresh() {
        dynamicConfigContainer.registerRefreshCallback(sensitiveConfig, this::refresh);
        IWordDeny deny = () -> {
            List<String> sub = WordDenySystem.getInstance().deny();
            sub.addAll(sensitiveConfig.getDeny());
            return sub;
        };

        IWordAllow allow = () -> {
            List<String> sub = WordAllowSystem.getInstance().allow();
            sub.addAll(sensitiveConfig.getAllow());
            return sub;
        };
        sensitiveWordBs = SensitiveWordBs.newInstance()
                .wordDeny(deny)
                .wordAllow(allow)
                .init();
        log.info("Sensitive words initialized!");
    }

    /**
     * Checks whether the text contains sensitive words.
     *
     * @param txt The text to be checked.
     * @return A list of sensitive words hit.
     */
    public List<String> contains(String txt) {
        if (!BooleanUtils.isTrue(sensitiveConfig.getEnable())) {
            return Collections.emptyList();
        }

        List<String> ans = sensitiveWordBs.findAll(txt);
        if (CollectionUtils.isEmpty(ans)) {
            return ans;
        }

        // Increment hit count for sensitive words
        RedisClient.PipelineAction action = RedisClient.pipelineAction();
        ans.forEach(key -> action.add(SENSITIVE_WORD_CNT_PREFIX, key, (connection, k, v) -> connection.hIncrBy(k, v, 1)));
        action.execute();
        return ans;
    }


    /**
     * Retrieves sensitive words hit count.
     *
     * @return A map containing sensitive words and their hit count.
     */
    public Map<String, Integer> getHitSensitiveWords() {
        return RedisClient.hGetAll(SENSITIVE_WORD_CNT_PREFIX, Integer.class);
    }

    /**
     * Removes a sensitive word.
     *
     * @param word The sensitive word to remove.
     */
    public void removeSensitiveWord(String word) {
        RedisClient.hDel(SENSITIVE_WORD_CNT_PREFIX, word);
    }

    /**
     * Replaces sensitive words in the text.
     *
     * @param txt The text to replace sensitive words in.
     * @return The text with sensitive words replaced.
     */
    public String replace(String txt) {
        if (BooleanUtils.isTrue(sensitiveConfig.getEnable())) {
            return sensitiveWordBs.replace(txt);
        }
        return txt;
    }

    /**
     * Finds all sensitive words in the text.
     *
     * @param txt The text to search for sensitive words in.
     * @return A list of sensitive words found in the text.
     */
    public List<String> findAll(String txt) {
        return sensitiveWordBs.findAll(txt);
    }
}

