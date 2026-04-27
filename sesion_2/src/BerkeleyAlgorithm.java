package src;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Demonstrates Berkeley's Clock Synchronization Algorithm.
 */
public class BerkeleyAlgorithm {
    private static final Logger logger = Logger.getLogger(BerkeleyAlgorithm.class.getName());

    /**
     * Calculates the time adjustments for each node according to Berkeley's algorithm.
     * 
     * @param serverTime The time of the daemon/master.
     * @param nodeTimes The times of the participating clients.
     * @return Array of adjustments. The first element is the adjustment for the server, 
     *         subsequent elements are for the clients.
     */
    public static long[] calculateAdjustments(long serverTime, long[] nodeTimes) {
        int n = nodeTimes.length + 1; // Clients + 1 server
        long sum = serverTime;
        for (long t : nodeTimes) {
            sum += t;
        }
        
        long average = sum / n;
        
        long[] adjustments = new long[n];
        adjustments[0] = average - serverTime;
        for (int i = 0; i < nodeTimes.length; i++) {
            adjustments[i + 1] = average - nodeTimes[i];
        }
        
        return adjustments;
    }

    public static void testBerkeleyAlgorithm() {
        long[] clients = {3000, 3050};
        long server = 2950;
        // Average: (2950 + 3000 + 3050) / 3 = 9000 / 3 = 3000
        // Expected adjustments: Server: +50, Client 1: 0, Client 2: -50
        
        long[] adjustments = calculateAdjustments(server, clients);
        if (adjustments[0] != 50) throw new AssertionError("Test failed for server adjustment");
        if (adjustments[1] != 0) throw new AssertionError("Test failed for client 1 adjustment");
        if (adjustments[2] != -50) throw new AssertionError("Test failed for client 2 adjustment");
        
        logger.info("All tests passed");
    }

    public static void main(String[] args) {
        testBerkeleyAlgorithm();

        long serverTime = 3000;
        long[] clientTimes = {2980, 3015, 3025}; // 3 clients

        logger.info("Initial Server Time: " + serverTime);
        logger.info("Initial Client Times: " + Arrays.toString(clientTimes));

        long[] adjustments = calculateAdjustments(serverTime, clientTimes);

        logger.info("Adjustments: " + Arrays.toString(adjustments));
        
        serverTime += adjustments[0];
        for (int i = 0; i < clientTimes.length; i++) {
            clientTimes[i] += adjustments[i + 1];
        }

        logger.info("Synchronized Server Time: " + serverTime);
        logger.info("Synchronized Client Times: " + Arrays.toString(clientTimes));
    }
}
