
package synchronizedDemo.synchronized01.practice.producerconsumer.blockingQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Entry point for the Producer-Consumer demonstration using BlockingQueue.
 *
 * <p>Creates a bounded queue with a capacity of three, starts the
 * producer and consumer threads, and waits for both to complete.</p>
 *
 * <p>Both threads receive the same queue instance, allowing the producer
 * to insert orders and the consumer to retrieve them safely.</p>
 */
public class OrderMain {

    /**
     * Starts the Producer-Consumer application.
     *
     * @param args command-line arguments
     * @throws InterruptedException if the main thread is interrupted
     *                              while waiting for either worker thread
     */
    public static void main(String[] args) throws InterruptedException {

        // Create a shared, bounded queue with a capacity of three.
        BlockingQueue<Integer> orders = new ArrayBlockingQueue<>(3);

        // Create the producer and consumer using the same queue.
        OrderConsumer consumer = new OrderConsumer(orders);
        OrderProducer producer = new OrderProducer(orders);

        // Create separate threads for the producer and consumer tasks.
        Thread producerThread = new Thread(producer, "producer-thread");
        Thread consumerThread = new Thread(consumer, "consumer-thread");

        // Start both worker threads.
        producerThread.start();
        consumerThread.start();

        // Wait for both worker threads to complete.
        producerThread.join();
        consumerThread.join();
    }
}

