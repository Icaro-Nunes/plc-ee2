
class PingPong {
    private boolean pingTurn = true;
    private static final int numMessages = 10;
    private final Object monitor = new Object();

    public void playPing() {
        synchronized (monitor) {
            try {
                while (!pingTurn) {
                    monitor.wait();
                }
                System.out.println("Ping");
                pingTurn = false;
                monitor.notify();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void playPong() {
        synchronized (monitor) {
            try {
                while (pingTurn) {
                    monitor.wait();
                }
                System.out.println("Pong");
                pingTurn = true;
                monitor.notify();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void playPingPong() {
        Thread pingThread = new Thread(() -> {
            for (int i = 0; i < numMessages; i++) {
                playPing();
            }
        });

        Thread pongThread = new Thread(() -> {
            for (int i = 0; i < numMessages; i++) {
                playPong();
            }
        });
        pingThread.start();
        pongThread.start();
        try {
            pingThread.join();
            pongThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        PingPong game = new PingPong();
        game.playPingPong();
    }
}
