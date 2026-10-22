package synchronizedDemo.synchronized01.practice.producerconsumer.arraylist;

import java.util.ArrayList;

/**
 * Implements a bounded buffer for the Producer-Consumer problem using an ArrayList.
 *
 * <p>The buffer allows a Producer to add orders and a Consumer to retrieve and
 * remove orders concurrently. Synchronization ensures that only one thread at
 * a time can modify or access the buffer through its synchronized methods.</p>
 *
 * <p>If the buffer is full, the Producer waits until space becomes available.
 * If the buffer is empty, the Consumer waits until an order is available.</p>
 *
 * <p>This class demonstrates the use of synchronized methods, wait(), and
 * notifyAll() for thread coordination.</p>
 */
public class OrderBuffer {

    /**
     * Maximum number of orders that the buffer can hold at a time.
     */
    private final int capacity = 3;

    /**
     * Stores the orders waiting to be processed.
     *
     * <p>ArrayList itself is not thread-safe. Access to this list is protected
     * by synchronizing on the OrderBuffer instance.</p>
     *
     * <p>The initial capacity of the ArrayList is set to 3, but this does not
     * enforce a maximum size. The capacity limit is enforced in placeOrder().</p>
     */
    private final ArrayList<Integer> orders = new ArrayList<>(capacity);

    /**
     * Creates an empty order buffer.
     */
    public OrderBuffer() {
    }

    /**
     * Adds an order to the buffer.
     *
     * <p>If the buffer is full, the calling Producer thread waits until another
     * thread removes an order and makes space available.</p>
     *
     * <p>The while loop rechecks the condition after waking up because a thread
     * must not assume that space is still available after being notified.</p>
     *
     * @param id the ID of the order to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void placeOrder(int id) throws InterruptedException {

        // Wait while the buffer is full.
        while (orders.size() == capacity) {
            wait();
        }

        // Add the order to the buffer.
        orders.add(id);

        // Notify waiting threads that the buffer's state has changed.
        notifyAll();
    }

    /**
     * Retrieves and removes the first order from the buffer.
     *
     * <p>If the buffer is empty, the calling Consumer thread waits until an
     * order becomes available.</p>
     *
     * <p>The first element is removed to preserve FIFO (First In, First Out)
     * processing order.</p>
     *
     * @return the ID of the order removed from the buffer
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized int processOrder() throws InterruptedException {

        // Wait while the buffer is empty.
        while (orders.isEmpty()) {
            wait();
        }

        // Remove and return the first order in the list.
        int processedOrder = orders.removeFirst();

        // Notify waiting threads that an order has been removed.
        notifyAll();

        return processedOrder;
    }
}
