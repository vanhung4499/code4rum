package com.hnv99.forum.front.article.vo;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import lombok.Data;

import java.util.List;

/**
 * Column View Object
 * Represents a list of columns along with sidebar information.
 */
@Data
public class ColumnVo {

    /**
     * List of columns
     */
    private PageListVo<ColumnDTO> columns;

    /**
     * Sidebar information
     */
    private List<SideBarDTO> sideBarItems;

}
