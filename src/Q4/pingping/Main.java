
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        int numMessages = 10;
        BlockingQueue<String> channel = new LinkedBlockingQueue<>();
        new Thread(() -> produtor(channel, numMessages)).start();
        consumidor(channel);
    }

    public static void produtor(BlockingQueue<String> channel, int numMessages) {
        String[] messages = new String[numMessages];
        for (int i = 0; i < numMessages; i++) {
            messages[i] = "ping";
        }
        for (String message : messages) {
            try {
                channel.put(message);
                System.out.println(message);
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            channel.put("acabou");
            System.out.println("Produtor End");
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void consumidor(BlockingQueue<String> channel) {
        int count = 1;
        while (true) {
            try {
                Thread.sleep(10);
                String message = channel.take();
                if (message.equals("acabou")) {
                    System.out.println("Consumidor End");
                    System.out.println();
                    break;
                } else {
                    System.out.println("[" + count + "] -> " + message);
                    System.out.println();
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}