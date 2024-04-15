package com.hnv99.forum.api.model.vo.recommend;

import com.hnv99.forum.api.model.enums.SidebarStyleEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * Sidebar DTO containing information for sidebar promotion.
 */
@Data
@Accessors(chain = true)
public class SideBarDTO {

    /**
     * Title of the sidebar.
     */
    private String title;

    /**
     * Subtitle of the sidebar.
     */
    private String subTitle;

    /**
     * Icon of the sidebar.
     */
    private String icon;

    /**
     * Image URL for the sidebar.
     */
    private String img;

    /**
     * URL link for the sidebar.
     */
    private String url;

    /**
     * Content of the sidebar.
     */
    private String content;

    /**
     * List of sidebar item DTOs.
     */
    private List<SideBarItemDTO> items;

    /**
     * Style of the sidebar, see {@link SidebarStyleEnum#getStyle()}.
     */
    private Integer style;
}

