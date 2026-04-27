package src;

import java.util.logging.Logger;

/**
 * Represents a Time Server in Cristian's Algorithm.
 */
class TimeServer {
    private long time;

    public TimeServer(long initialTime) {
        this.time = initialTime;
    }

    /**
     * Gets the current time of the server.
     * @return current server time
     */
    public long getTime() {
        return time;
    }
}

/**
 * Demonstrates Cristian's Clock Synchronization Algorithm.
 */
public class CristianAlgorithm {
    private static final Logger logger = Logger.getLogger(CristianAlgorithm.class.getName());

    /**
     * Calculates the synchronized time for the client.
     * 
     * @param serverTime The time returned by the server.
     * @param rtt The round-trip time.
     * @return The new synchronized time for the client.
     */
    public static long calculateSynchronizedTime(long serverTime, long rtt) {
        return serverTime + (rtt / 2);
    }

    public static void testCristianAlgorithm() {
        // Test case 1
        long expected = 1050;
        long actual = calculateSynchronizedTime(1000, 100);
        if (expected != actual) throw new AssertionError("Test 1 failed");
        
        logger.info("All tests passed");
    }

    public static void main(String[] args) {
        testCristianAlgorithm();

        TimeServer server = new TimeServer(5000); // Server time is 5000ms
        
        long t0 = 1000; // Client sends request at its own time 1000
        
        // Simulating network delay
        long networkDelay = 150;
        long serverTime = server.getTime() + networkDelay / 2; // Server time when processing
        
        long t1 = t0 + networkDelay; // Client receives response at 1150
        
        long rtt = t1 - t0;
        long syncedTime = calculateSynchronizedTime(serverTime, rtt);

        logger.info("Client T0: " + t0);
        logger.info("Client T1: " + t1);
        logger.info("Server Time returned: " + serverTime);
        logger.info("Calculated RTT: " + rtt);
        logger.info("Synchronized Time: " + syncedTime);
    }
}
