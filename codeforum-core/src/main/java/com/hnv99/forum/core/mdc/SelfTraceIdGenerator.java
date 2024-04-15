package com.hnv99.forum.core.mdc;

import com.hnv99.forum.core.util.IpUtil;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Custom traceId generator
 * <p>
 * Generation rules refer to <a href="https://help.aliyun.com/document_detail/151840.html"/>
 */
@Slf4j
public class SelfTraceIdGenerator {
    private final static Integer MIN_AUTO_NUMBER = 1000;
    private final static Integer MAX_AUTO_NUMBER = 10000;
    private static volatile Integer autoIncreaseNumber = MIN_AUTO_NUMBER;

    /**
     * <p>
     * Generates a 32-character traceId, following the rule: server IP + time when the ID is generated + auto-increment sequence + current process ID
     * IP 8 characters: 39.105.208.175 -> 2769d0af
     * Time when the ID is generated 13 characters: millisecond timestamp -> 1403169275002
     * Current process ID 5 characters: PID
     * Auto-increment sequence 4 characters: cyclic from 1000 to 9999
     * </p>
     *
     * @return ac13e001.1685348263825.095001000
     */
    public static String generate() {
        StringBuilder traceId = new StringBuilder();
        try {
            // 1. IP - 8
            traceId.append(convertIp(IpUtil.getLocalIp4Address())).append(".");
            // 2. Timestamp - 13
            traceId.append(Instant.now().toEpochMilli()).append(".");
            // 3. Current process ID - 5
            traceId.append(getProcessId());
            // 4. Auto-increment sequence - 4
            traceId.append(getAutoIncreaseNumber());
        } catch (Exception e) {
            log.error("Error generating trace id!", e);
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        return traceId.toString();
    }

    /**
     * Converts IP address to hexadecimal - 8 characters
     *
     * @param ip 39.105.208.175
     * @return 2769d0af
     */
    private static String convertIp(String ip) {
        return Splitter.on(".").splitToStream(ip)
                .map(s -> String.format("%02x", Integer.valueOf(s)))
                .collect(Collectors.joining());
    }

    /**
     * Ensure the auto-increment sequence cycles between 1000 and 9999 - 4 characters
     *
     * @return Auto-increment sequence number
     */
    private static int getAutoIncreaseNumber() {
        if (autoIncreaseNumber >= MAX_AUTO_NUMBER) {
            autoIncreaseNumber = MIN_AUTO_NUMBER;
            return autoIncreaseNumber;
        } else {
            return autoIncreaseNumber++;
        }
    }

    /**
     * @return 5-character current process ID
     */
    private static String getProcessId() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String processId = runtime.getName().split("@")[0];
        return String.format("%05d", Integer.parseInt(processId));
    }

    public static void main(String[] args) {
        String t = generate();
        System.out.println(t);
        String t2 = generate();
        System.out.println(t2);

        String trace = SkyWalkingTraceIdGenerator.generate();
        System.out.println(trace);
    }
}
