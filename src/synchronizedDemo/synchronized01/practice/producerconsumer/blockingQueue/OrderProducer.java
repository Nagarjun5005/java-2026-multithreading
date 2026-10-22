 package synchronizedDemo.synchronized01.practice.producerconsumer.blockingQueue;

import java.util.concurrent.BlockingQueue;

/**
 * Producer component in the Producer-Consumer pattern.
 *
 * <p>This class generates 10 orders, with IDs ranging from 101 to 110,
 * and inserts them into a shared BlockingQueue.</p>
 *
 * <p>The BlockingQueue handles thread-safe access and coordination.
 * If the queue is full, the put() method blocks until space becomes
 * available.</p>
 */
public class OrderProducer implements Runnable {

    /**
     * Shared, thread-safe queue into which orders are inserted.
     */
    private BlockingQueue<Integer> orders;

    /**
     * Creates a producer using the shared order queue.
     *
     * @param orders the BlockingQueue shared with the consumer
     */
    public OrderProducer(BlockingQueue<Integer> orders) {
        this.orders = orders;
    }

    /**
     * Executes the producer task.
     *
     * <p>Generates and inserts 10 orders into the queue.
     * If interrupted while waiting for queue capacity, the method
     * restores the thread's interrupted status and stops producing.</p>
     */
    @Override
    public void run() {
        try {
            // Produce 10 orders, from 101 through 110.
            for (int i = 101; i <= 110; i++) {

                // Insert the order, waiting if the queue is full.
                orders.put(i);

                // Print after the order has been accepted by the queue.
                System.out.println("Order placed --->" + i);
            }

        } catch (InterruptedException e) {
            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}
