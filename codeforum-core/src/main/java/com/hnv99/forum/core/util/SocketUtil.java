package com.hnv99.forum.core.util;

import javax.net.ServerSocketFactory;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Utility class for working with sockets.
 */
public class SocketUtil {

    /**
     * Check if a port is available for binding.
     *
     * @param port The port to check for availability.
     * @return true if the port is available, false otherwise.
     */
    public static boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1);
            serverSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Random random = new Random();

    private static int findRandomPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        return minPort + random.nextInt(portRange + 1);
    }

    /**
     * Find an available TCP port within the specified range, or return the default port if available.
     *
     * @param minPort The minimum port number in the range.
     * @param maxPort The maximum port number in the range.
     * @param defaultPort The default port to use if no other ports are available.
     * @return The available port number.
     * @throws IllegalArgumentException if maxPort is less than or equal to minPort.
     * @throws IllegalStateException if no available port is found within the specified range after multiple attempts.
     */
    public static int findAvailableTcpPort(int minPort, int maxPort, int defaultPort) {
        if (isPortAvailable(defaultPort)) {
            return defaultPort;
        }

        if (maxPort <= minPort) {
            throw new IllegalArgumentException("maxPort should be greater than minPort!");
        }
        int portRange = maxPort - minPort;
        int searchCounter = 0;

        while (searchCounter <= portRange) {
            int candidatePort = findRandomPort(minPort, maxPort);
            ++searchCounter;
            if (isPortAvailable(candidatePort)) {
                return candidatePort;
            }
        }

        throw new IllegalStateException(String.format("Could not find an available %s port in the range [%d, %d] after %d attempts", SocketUtil.class.getName(), minPort, maxPort, searchCounter));
    }
}

