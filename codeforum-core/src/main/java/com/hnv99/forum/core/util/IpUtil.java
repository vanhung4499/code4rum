package com.hnv99.forum.core.util;

import com.github.hui.quick.plugin.base.file.FileWriteUtil;
import com.hnv99.forum.core.region.IpRegionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for handling IP-related operations.
 *
 * @author YiHui
 * @date 2022/7/6
 */
@Slf4j
public class IpUtil {
    private static final String UNKNOWN = "unKnown";

    public static final String DEFAULT_IP = "127.0.0.1";

    /**
     * Get all IPv4 addresses of the local machine.
     *
     * @return List of Inet4Address
     */
    private static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (ObjectUtils.isEmpty(networkInterfaces)) {
            return addresses;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (!isValidInterface(networkInterface)) {
                continue;
            }

            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (isValidAddress(inetAddress)) {
                    addresses.add((Inet4Address) inetAddress);
                }
            }
        }
        return addresses;
    }

    /**
     * Filter loopback interfaces, point-to-point interfaces, inactive interfaces, virtual interfaces, and require interface names to start with "eth" or "ens".
     *
     * @param ni Network Interface
     * @return true if the interface meets the requirements, false otherwise
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * Check if the address is IPv4, a site-local address, and not a loopback address.
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    /**
     * Obtain an IP address uniquely through Socket.
     * This method usually works fine even when there are multiple network cards. It does not require the external IP address like 8.8.8.8 to be reachable.
     *
     * @return Optional<Inet4Address>
     */
    private static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException networkInterfaces) {
            throw new RuntimeException(networkInterfaces);
        }
        return Optional.empty();
    }

    private static String LOCAL_IP = null;

    /**
     * Get the local IPv4 address.
     *
     * @return String representing the local IPv4 address
     */
    public static String getLocalIp4Address() throws SocketException {
        if (LOCAL_IP != null) {
            return LOCAL_IP;
        }

        final List<Inet4Address> inet4Addresses = getLocalIp4AddressFromNetworkInterface();
        if (inet4Addresses.size() != 1) {
            final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
            LOCAL_IP = ipBySocketOpt.map(Inet4Address::getHostAddress).orElseGet(() -> inet4Addresses.isEmpty() ? DEFAULT_IP : inet4Addresses.get(0).getHostAddress());
            return LOCAL_IP;
        }
        LOCAL_IP = inet4Addresses.get(0).getHostAddress();
        return LOCAL_IP;
    }


    /**
     * Get the client's IP address from the request.
     *
     * @param request HttpServletRequest
     * @return Client's IP address
     */
    public static String getClientIp(HttpServletRequest request) {
        try {
            String xIp = request.getHeader("X-Real-IP");
            String xFor = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)) {
                int index = xFor.indexOf(",");
                if (index != -1) {
                    return xFor.substring(0, index);
                } else {
                    return xFor;
                }
            }
            xFor = xIp;
            if (StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)) {
                return xFor;
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getRemoteAddr();
            }

            if ("localhost".equalsIgnoreCase(xFor) || "127.0.0.1".equalsIgnoreCase(xFor) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(xFor)) {
                return getLocalIp4Address();
            }
            return xFor;
        } catch (Exception e) {
            log.error("Error getting remote IP!", e);
            return "x.0.0.1";
        }
    }

    /**
     * IP location library path
     * <a href="https://github.com/lionsoul2014/ip2region/tree/master/binding/java"/>
     */
    private static final String dbPath = "data/ip2region.xdb";
    private static String tmpPath = null;
    private static volatile byte[] vIndex = null;

    private static void initVIndex() {
        if (vIndex == null) {
            synchronized (IpUtil.class) {
                if (vIndex == null) {
                    try {
                        String file = IpUtil.class.getClassLoader().getResource(dbPath).getFile();
                        if (file.contains(".jar!")) {
                            FileWriteUtil.FileInfo tmpFile = new FileWriteUtil.FileInfo("/tmp/data", "ip2region", "xdb");
                            tmpPath = tmpFile.getAbsFile();
                            if (!new File(tmpPath).exists()) {
                                FileWriteUtil.saveFileByStream(IpUtil.class.getClassLoader().getResourceAsStream(dbPath), tmpFile);
                            }
                        } else {
                            tmpPath = file;
                        }
                        vIndex = Searcher.loadVectorIndexFromFile(tmpPath);
                    } catch (Exception e) {
                        log.error("Failed to load vector index from {}\n", dbPath, e);
                    }
                }
            }
        }
    }

    /**
     * Get location information based on IP address: Country|Region|Province|City|ISP
     * If the corresponding location does not exist, it returns 0.
     *
     * @param ip IP address
     * @return IpRegionInfo
     */
    public static IpRegionInfo getLocationByIp(String ip) {
        initVIndex();
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithVectorIndex(tmpPath, vIndex);
            return new IpRegionInfo(searcher.search(ip));
        } catch (Exception e) {
            log.error("Failed to create vectorIndex cached searcher with {}: {}\n", dbPath, e);
            return new IpRegionInfo("");
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    log.error("Failed to close file:{}\n", dbPath, e);
                }
            }
        }
    }
}
