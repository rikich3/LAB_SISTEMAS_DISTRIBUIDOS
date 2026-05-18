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
     * 
     * @return current server time
     */
    public long getTime() {
        return time;
    }
}

/**
 * Demonstrates Cristian's Clock Synchronization Algorithm with threads.
 * Multiple clients synchronize independently with a shared server.
 */
public class CristianAlgorithmThreads {
    private static final Logger logger = Logger.getLogger(CristianAlgorithmThreads.class.getName());

    /**
     * Calculates the synchronized time for the client.
     * 
     * @param serverTime The time returned by the server.
     * @param rtt        The round-trip time.
     * @return The new synchronized time for the client.
     */
    public static long calculateSynchronizedTime(long serverTime, long rtt) {
        return serverTime + (rtt / 2);
    }

    public static void main(String[] args) {
        TimeServer server = new TimeServer(5000); // Server time compartido

        // Crear 5 clientes (threads) que sincronizan simultaneamente
        Thread[] clients = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int clientId = i;
            clients[i] = new Thread(() -> {
                long t0 = System.currentTimeMillis(); // Client envía solicitud

                // Simular latencia de red variable (50-200ms)
                long networkDelay = 50 + (long) (Math.random() * 150);

                try {
                    Thread.sleep(networkDelay / 2); // Simular tiempo de viaje al servidor
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long serverTime = server.getTime();

                try {
                    Thread.sleep(networkDelay / 2); // Simular tiempo de viaje de regreso
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long t1 = System.currentTimeMillis(); // Client recibe respuesta
                long rtt = t1 - t0;
                long syncedTime = calculateSynchronizedTime(serverTime, rtt);

                logger.info("===== Cliente " + clientId + " =====");
                logger.info("T0 (envío): " + t0);
                logger.info("T1 (recepción): " + t1);
                logger.info("Server Time: " + serverTime);
                logger.info("RTT: " + rtt + "ms");
                logger.info("Tiempo sincronizado: " + syncedTime);
                logger.info("");
            });
            clients[i].start();
        }

        // Esperar a que todos los clientes terminen
        for (Thread client : clients) {
            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("Sincronización completada");
    }
}
