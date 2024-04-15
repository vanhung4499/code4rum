package com.hnv99.forum.api.model.vo;

import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    /**
     * Business status code
     */
    @ApiModelProperty(value = "Status code, 0 indicates successful return, other values indicate exceptions", required = true, example = "0")
    private int code;

    /**
     * Description message
     */
    @ApiModelProperty(value = "For successful returns, it's 'ok', for exceptions, it's the description text", required = true, example = "ok")
    private String msg;

    public static Status newStatus(int code, String msg) {
        return new Status(code, msg);
    }

    public static Status newStatus(StatusEnum status, Object... msgs) {
        String msg;
        if (msgs.length > 0) {
            msg = String.format(status.getMsg(), msgs);
        } else {
            msg = status.getMsg();
        }
        return newStatus(status.getCode(), msg);
    }
}

