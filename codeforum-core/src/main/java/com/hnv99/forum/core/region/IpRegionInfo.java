package com.hnv99.forum.core.region;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * IP region information.
 */
@Data
public class IpRegionInfo {
    /**
     * Country or region.
     */
    private String country;
    /**
     * Area.
     */
    private String region;
    /**
     * Province.
     */
    private String province;
    /**
     * City.
     */
    private String city;
    /**
     * Internet Service Provider (ISP).
     */
    private String isp;

    public IpRegionInfo(String info) {
        String[] cells = StringUtils.split(info, "|");
        if (cells.length < 5) {
            country = "";
            region = "";
            province = "";
            city = "";
            isp = "";
            return;
        }
        country = "0".equals(cells[0]) ? "" : cells[0];
        region = "0".equals(cells[1]) ? "" : cells[1];
        province = "0".equals(cells[2]) ? "" : cells[2];
        city = "0".equals(cells[3]) ? "" : cells[3];
        isp = "0".equals(cells[4]) ? "" : cells[4];
    }

    /**
     * Convert region information to a string format.
     *
     * @return Region information as a string.
     */
    public String toRegionStr() {
        if (StringUtils.isNotBlank(province)) {
            // return country + province
            return country + "·" + province;
        }
        return country;
    }
}

