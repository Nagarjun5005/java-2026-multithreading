 package synchronizedDemo.synchronized01.practice.producerconsumer.blockingQueue;

import java.util.concurrent.BlockingQueue;

/**
 * Consumer component in the Producer-Consumer pattern.
 *
 * <p>This class retrieves orders from a shared BlockingQueue.
 * It consumes exactly 10 orders, provided the thread is not interrupted
 * before completing its work.</p>
 *
 * <p>The BlockingQueue handles thread-safe access and coordination.
 * If the queue is empty, the take() method blocks until an order
 * becomes available.</p>
 */
public class OrderConsumer implements Runnable {

    /**
     * Shared, thread-safe queue from which orders are consumed.
     */
    private BlockingQueue<Integer> orders;

    /**
     * Creates a consumer using the shared order queue.
     *
     * @param orders the BlockingQueue shared with the producer
     */
    public OrderConsumer(BlockingQueue<Integer> orders) {
        this.orders = orders;
    }

    /**
     * Executes the consumer task.
     *
     * <p>Retrieves and prints 10 orders from the queue.
     * If interrupted while waiting for an order, the method restores
     * the thread's interrupted status and stops processing.</p>
     */
    @Override
    public void run() {
        try {
            // Consume exactly 10 orders.
            for (int i = 0; i < 10; i++) {

                // Wait for and retrieve the next available order.
                Integer processedOrder = orders.take();

                // Print the consumed order ID.
                System.out.println("Order consumed ---->" + processedOrder);
            }

        } catch (InterruptedException e) {
            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}

