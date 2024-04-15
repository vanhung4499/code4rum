package com.hnv99.forum.service.user.service.help;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * Password Encoder, can be replaced with PasswordEncoder after integrating Spring Security
 */
@Component
public class UserPwdEncoder {
    /**
     * Password salting. It is recommended to use a unique salt for each user to enhance security.
     */
    @Value("${security.salt}")
    private String salt;

    @Value("${security.salt-index}")
    private Integer saltIndex;

    public boolean match(String plainPwd, String encPwd) {
        return Objects.equals(encodePwd(plainPwd), encPwd);
    }

    /**
     * Processing plaintext password
     *
     * @param plainPwd
     * @return
     */
    public String encodePwd(String plainPwd) {
        if (plainPwd.length() > saltIndex) {
            plainPwd = plainPwd.substring(0, saltIndex) + salt + plainPwd.substring(saltIndex);
        } else {
            plainPwd = plainPwd + salt;
        }
        return DigestUtils.md5DigestAsHex(plainPwd.getBytes(StandardCharsets.UTF_8));
    }

}

