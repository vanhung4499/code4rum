package com.hnv99.forum.core.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Utility class for handling alarms.
 *
 * It extends AppenderBase<ILoggingEvent> to provide custom logging appenders.
 */
public class AlarmUtil extends AppenderBase<ILoggingEvent> {
    /**
     * The interval between two consecutive alarms (in milliseconds).
     */
    private static final long INTERVAL = 10 * 1000 * 60;

    /**
     * The timestamp of the last alarm.
     */
    private long lastAlarmTime = 0;

    /**
     * Appends the log event to send an alarm if necessary.
     *
     * @param iLoggingEvent The log event to append.
     */
    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (canAlarm()) {
            EmailUtil.sendMail(iLoggingEvent.getLoggerName(),
                    SpringUtil.getConfig("alarm.user", "xhhuiblog@163.com"),
                    iLoggingEvent.getFormattedMessage());
        }
    }

    /**
     * Checks if an alarm can be sent based on the last alarm time and the defined interval.
     *
     * @return true if an alarm can be sent, false otherwise.
     */
    private boolean canAlarm() {
        // Implementing a simple frequency filter: only allow one alarm to be sent per minute.
        long now = System.currentTimeMillis();
        if (now - lastAlarmTime >= INTERVAL) {
            lastAlarmTime = now;
            return true;
        } else {
            return false;
        }
    }
}
