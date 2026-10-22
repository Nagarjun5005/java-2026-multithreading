package synchronizedDemo.synchronized01.practice.producerconsumer.arraylist;

/**
 * Entry point for the Producer-Consumer application using an ArrayList.
 *
 * <p>This class creates a shared OrderBuffer, a Producer, and a Consumer.
 * The Producer generates orders, while the Consumer retrieves and processes
 * them concurrently.</p>
 *
 * <p>The OrderBuffer coordinates both threads using synchronized methods,
 * wait(), and notifyAll().</p>
 */
public class OrderMain {

    /**
     * Starts the Producer and Consumer threads.
     *
     * <p>A single OrderBuffer instance is shared between both threads to
     * allow safe communication through the bounded buffer.</p>
     *
     * <p>The main thread waits for both worker threads to finish using join()
     * before terminating.</p>
     *
     * @param args command-line arguments (not used)
     * @throws InterruptedException if the main thread is interrupted while
     *                              waiting for either worker thread to finish
     */
    public static void main(String[] args) throws InterruptedException {

        // Create one shared buffer for storing orders.
        OrderBuffer orderBuffer = new OrderBuffer();

        // Create Producer and Consumer using the same buffer.
        OrderProducer producer = new OrderProducer(orderBuffer);
        OrderConsumer consumer = new OrderConsumer(orderBuffer);

        // Create separate threads for concurrent execution.
        Thread producerThread = new Thread(producer, "Producer-Thread");
        Thread consumerThread = new Thread(consumer, "Consumer-Thread");

        // Start both threads.
        producerThread.start();
        consumerThread.start();

        // Wait for the Producer to finish producing orders.
        producerThread.join();

        // Wait for the Consumer to finish consuming orders.
        consumerThread.join();
    }
}
