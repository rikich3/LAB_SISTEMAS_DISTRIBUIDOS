package src;

import java.util.logging.Logger;

/**
 * Demonstrates Berkeley's Clock Synchronization Algorithm with threads.
 * A master daemon collects times from all clients, calculates average,
 * and distributes adjustments back to all clients simultaneously.
 */
public class BerkeleyAlgorithmThreads {
    private static final Logger logger = Logger.getLogger(BerkeleyAlgorithmThreads.class.getName());

    /**
     * Calculates the time adjustments for each node according to Berkeley's
     * algorithm.
     * 
     * @param serverTime The time of the daemon/master.
     * @param nodeTimes  The times of the participating clients.
     * @return Array of adjustments. The first element is the adjustment for the
     *         server,
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

    static class TimeClient {
        long time;
        int id;
        long adjustment = 0;
        boolean synced = false;

        TimeClient(int id, long initialTime) {
            this.id = id;
            this.time = initialTime;
        }
    }

    public static void main(String[] args) {
        // Master (daemon) time
        long masterTime = 3000;

        // Crear 5 clientes con tiempos iniciales diferentes
        TimeClient[] clients = new TimeClient[5];
        long[] initialTimes = { 2980, 3015, 3025, 2990, 3010 };

        for (int i = 0; i < 5; i++) {
            clients[i] = new TimeClient(i, initialTimes[i]);
        }

        logger.info("===== BERKELEY ALGORITHM WITH THREADS =====");
        logger.info("Master Time: " + masterTime);
        for (int i = 0; i < clients.length; i++) {
            logger.info("Client " + i + " Initial Time: " + clients[i].time);
        }
        logger.info("");

        // Phase 1: Master recopila tiempos de todos los clientes
        long[] collectedTimes = new long[clients.length];
        Thread[] clientThreads = new Thread[clients.length];

        logger.info("===== PHASE 1: Master collecting times =====");
        for (int i = 0; i < clients.length; i++) {
            final int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 100)); // Simular delay
                    synchronized (clients[clientId]) {
                        collectedTimes[clientId] = clients[clientId].time;
                        logger.info("Master recopila tiempo de Cliente " + clientId + ": " + collectedTimes[clientId]);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            clientThreads[i].start();
        }

        // Esperar a que todos los clientes reporten su tiempo
        for (Thread t : clientThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("");
        logger.info("===== PHASE 2: Master calculates average =====");

        // Phase 2: Master calcula ajustes
        long[] adjustments = calculateAdjustments(masterTime, collectedTimes);

        long sum = masterTime;
        for (long t : collectedTimes)
            sum += t;
        long average = sum / (collectedTimes.length + 1);
        logger.info("Average = " + average);
        logger.info("");

        logger.info("Adjustments calculated:");
        logger.info("Master adjustment: " + adjustments[0]);
        for (int i = 0; i < collectedTimes.length; i++) {
            logger.info("Client " + i + " adjustment: " + adjustments[i + 1]);
        }
        logger.info("");

        // Phase 3: Master envía ajustes a todos los clientes
        logger.info("===== PHASE 3: Master distributes adjustments =====");
        clientThreads = new Thread[clients.length];

        for (int i = 0; i < clients.length; i++) {
            final int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 100)); // Simular delay de red
                    synchronized (clients[clientId]) {
                        clients[clientId].adjustment = adjustments[clientId + 1];
                        clients[clientId].time += clients[clientId].adjustment;
                        clients[clientId].synced = true;
                        logger.info("Cliente " + clientId + " recibe ajuste: " + clients[clientId].adjustment +
                                " → Nuevo tiempo: " + clients[clientId].time);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            clientThreads[i].start();
        }

        // Esperar a que todos se sincronicen
        for (Thread t : clientThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("");
        logger.info("===== SYNCHRONIZATION COMPLETED =====");
        masterTime += adjustments[0];
        logger.info("Master Time synchronized: " + masterTime);

        for (TimeClient client : clients) {
            logger.info("Client " + client.id + " Time synchronized: " + client.time);
        }
    }
}
