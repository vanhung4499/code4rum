package com.hnv99.forum.api.model.vo.recommend;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * Sidebar item DTO containing information for individual sidebar items.
 */
@Data
@Accessors(chain = true)
public class SideBarItemDTO {

    /**
     * Title of the sidebar item.
     */
    private String title;

    /**
     * Name of the sidebar item.
     */
    private String name;

    /**
     * URL link for the sidebar item.
     */
    private String url;

    /**
     * Image URL for the sidebar item.
     */
    private String img;

    /**
     * Time information for the sidebar item.
     */
    private Long time;

    /**
     * List of tags for the sidebar item.
     */
    private List<Integer> tags;

    /**
     * Rating and visit information for the sidebar item.
     */
    private RateVisitDTO visit;
}

