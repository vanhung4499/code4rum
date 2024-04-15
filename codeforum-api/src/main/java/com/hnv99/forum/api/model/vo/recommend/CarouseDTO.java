package com.hnv99.forum.api.model.vo.recommend;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Data transfer object for carousel information.
 */
@Data
@Accessors(chain = true)
public class CarouseDTO implements Serializable {

    private static final long serialVersionUID = 1048555496974144842L;

    /**
     * Description of the carousel item.
     */
    private String name;

    /**
     * URL of the carousel image.
     */
    private String imgUrl;

    /**
     * URL to which the carousel item should redirect.
     */
    private String actionUrl;
}

