package com.hnv99.forum.service.config.repository.dao;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.vo.article.dto.DictCommonDTO;
import com.hnv99.forum.service.config.converter.DictCommonConverter;
import com.hnv99.forum.service.config.repository.entity.DictCommonDO;
import com.hnv99.forum.service.config.repository.mapper.DictCommonMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class DictCommonDao extends ServiceImpl<DictCommonMapper, DictCommonDO> {

    /**
     * Get all dictionary lists
     * @return
     */
    public List<DictCommonDTO> getDictList() {
        List<DictCommonDO> list = lambdaQuery().list();
        return DictCommonConverter.toDTOs(list);
    }
}
