package com.hnv99.forum.api.model.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ConfigRefreshEvent extends ApplicationEvent {
    private String key;
    private String val;


    public ConfigRefreshEvent(Object source, String key, String value) {
        super(source);
        this.key = key;
        this.val = value;
    }
}
