package com.hnv99.forum.api.model.enums.site;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SiteVisitStatisticsEnum {
    PV(1, "Page views"),
    UV(2, "Unique visitors"),
    VV(3, "Visit times"),
            ;

    private int type;
    private String desc;
}
