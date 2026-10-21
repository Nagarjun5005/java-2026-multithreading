package synchronizedDemo.synchronized01.practice.producerconsumer.multipleSlotBuffer;

/*
 * Class: OrderBuffer
 *
 * Purpose:
 * Implements a fixed-size, array-based circular buffer for the
 * Producer-Consumer problem.
 *
 * The buffer can store up to 3 orders simultaneously.
 *
 * The Producer inserts orders using placeOrder().
 * The Consumer retrieves orders using processOrder().
 *
 * Synchronization is achieved using:
 * 1. synchronized methods
 * 2. wait()
 * 3. notifyAll()
 *
 * FIFO (First In, First Out) is maintained, meaning orders
 * are consumed in the same order in which they were inserted.
 */
public class OrderBuffer {

    /*
     * putIndex:
     * Indicates the array position where the next order
     * will be inserted by the Producer.
     *
     * Initially, the Producer inserts at index 0.
     */
    private int putIndex = 0;

    /*
     * takeIndex:
     * Indicates the array position from which the next order
     * will be retrieved by the Consumer.
     *
     * Initially, the Consumer retrieves from index 0.
     */
    private int takeIndex = 0;

    /*
     * count:
     * Tracks the number of orders currently stored in the buffer.
     *
     * count == 0:
     * Buffer is empty; Consumer must wait.
     *
     * count == capacity:
     * Buffer is full; Producer must wait.
     */
    private int count = 0;

    /*
     * capacity:
     * Maximum number of orders the buffer can store.
     *
     * The capacity is fixed at 3.
     */
    private final int capacity = 3;

    /*
     * orders:
     * Array used to store order IDs.
     *
     * Each array position can hold one order ID.
     * The array does not grow dynamically.
     *
     * Circular indexing allows previously used positions
     * to be reused without shifting array elements.
     */
    private int[] orders = new int[capacity];


    /*
     * Method: placeOrder()
     *
     * Purpose:
     * Inserts an order into the buffer.
     *
     * @param id the ID of the order to be inserted
     *
     * @throws InterruptedException if the thread is interrupted
     *                              while waiting
     *
     * Execution flow:
     *
     * 1. Check whether the buffer is full.
     * 2. If full, wait until the Consumer removes an order.
     * 3. Insert the order at orders[putIndex].
     * 4. Advance putIndex using circular indexing.
     * 5. Increment count.
     * 6. Notify waiting threads that the buffer has changed.
     *
     * Synchronization:
     * The synchronized keyword ensures that only one thread
     * at a time can execute a synchronized method on this
     * OrderBuffer instance.
     *
     * wait():
     * Releases this object's monitor while the Producer waits,
     * allowing the Consumer to access the buffer.
     *
     * notifyAll():
     * Wakes threads waiting on this object's monitor.
     * They must reacquire the monitor and recheck their
     * conditions before proceeding.
     */
    public synchronized void placeOrder(int id)
            throws InterruptedException {

        // Wait while the buffer is full.
        while (count == capacity) {
            wait();
        }

        // Insert the order at the current insertion position.
        orders[putIndex] = id;

        // Move to the next index, wrapping around when necessary.
        putIndex = (putIndex + 1) % capacity;

        // Increase the number of orders stored in the buffer.
        count++;

        // Notify waiting threads that the buffer has changed.
        notifyAll();
    }


    /*
     * Method: processOrder()
     *
     * Purpose:
     * Retrieves the next order from the buffer and returns
     * its ID to the Consumer.
     *
     * @return the ID of the retrieved order
     *
     * @throws InterruptedException if the thread is interrupted
     *                              while waiting
     *
     * Execution flow:
     *
     * 1. Check whether the buffer is empty.
     * 2. If empty, wait until the Producer inserts an order.
     * 3. Retrieve the order at orders[takeIndex].
     * 4. Advance takeIndex using circular indexing.
     * 5. Decrement count.
     * 6. Notify waiting threads that the buffer has changed.
     * 7. Return the retrieved order ID.
     *
     * Note:
     * The retrieved value is stored in a local variable before
     * the index advances. The array slot does not need to be
     * reset because count and the indices track the logical
     * contents of the buffer.
     */
    public synchronized int processOrder()
            throws InterruptedException {

        // Wait while the buffer is empty.
        while (count == 0) {
            wait();
        }

        // Retrieve the next order from the buffer.
        int processedOrder = orders[takeIndex];

        // Move to the next index, wrapping around when necessary.
        takeIndex = (takeIndex + 1) % capacity;

        // Decrease the number of orders stored in the buffer.
        count--;

        // Notify waiting threads that the buffer has changed.
        notifyAll();

        // Return the retrieved order ID.
        return processedOrder;
    }
}
