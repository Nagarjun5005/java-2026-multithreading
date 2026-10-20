package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer.orderSystem;

/*
 * ============================================================================
 * CLASS: OrderBuffer
 * ============================================================================
 *
 * PURPOSE:
 * Represents a single-slot buffer shared between an OrderProducer thread
 * and an OrderConsumer thread.
 *
 * The buffer stores one order ID at a time. The Producer places orders
 * into the buffer, and the Consumer retrieves and processes them.
 *
 * PROBLEM BEING SOLVED:
 * Since both threads access the same data, improper coordination could cause:
 *
 *   1. The Producer to overwrite an order that has not been processed.
 *   2. The Consumer to attempt to process an order when the buffer is empty.
 *   3. Race conditions when both threads access the shared state.
 *
 * SOLUTION:
 * This class uses synchronized methods, wait(), notifyAll(), and a boolean
 * flag to coordinate access between the Producer and Consumer.
 *
 * BUFFER RULES:
 *
 *   1. The Producer can place an order only when the buffer is empty.
 *   2. The Consumer can process an order only when the buffer is full.
 *   3. If the buffer is full, the Producer must wait.
 *   4. If the buffer is empty, the Consumer must wait.
 *
 * IMPORTANT:
 * Both threads must use the same OrderBuffer instance so that they share
 * the same data and coordinate using the same object's monitor.
 * ============================================================================
 */
public class OrderBuffer {

    /*
     * ------------------------------------------------------------------------
     * INSTANCE VARIABLE: orderId
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Stores the order ID currently held in the buffer.
     *
     * USAGE:
     *   - The Producer updates this variable when placing an order.
     *   - The Consumer reads this variable when processing an order.
     *
     * IMPORTANT:
     * The orderId variable may retain its previous value even after the
     * Consumer processes the order. The available flag determines whether
     * the stored value represents an order that is waiting to be processed.
     */
    private int orderId;


    /*
     * ------------------------------------------------------------------------
     * INSTANCE VARIABLE: available
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Indicates whether an order is currently available for the Consumer.
     *
     * STATES:
     *
     *   false -> Buffer is EMPTY.
     *            The Producer may place an order.
     *            The Consumer must wait.
     *
     *   true  -> Buffer is FULL.
     *            The Consumer may process the order.
     *            The Producer must wait.
     *
     * INITIAL STATE:
     * The buffer starts empty, so available is initialized to false.
     *
     * STATE TRANSITIONS:
     *
     *   Producer places an order:
     *       available = true
     *
     *   Consumer processes an order:
     *       available = false
     */
    private boolean available = false;


    /*
     * =========================================================================
     * METHOD: placeOrder(int id)
     * =========================================================================
     *
     * PURPOSE:
     * Places an order into the buffer.
     *
     * PARAMETER:
     * id - The order ID supplied by the Producer.
     *
     * SYNCHRONIZATION:
     * The synchronized keyword requires the Producer to acquire this
     * OrderBuffer object's monitor before entering the method.
     *
     * Only one thread at a time can execute a synchronized instance method
     * on this same OrderBuffer object.
     *
     * EXECUTION FLOW:
     *
     *   1. Acquire the OrderBuffer object's monitor.
     *
     *   2. Check whether the buffer is already full.
     *
     *   3. If available is true, the buffer contains an unprocessed order.
     *      The Producer must not overwrite it.
     *
     *   4. Call wait() while the buffer is full.
     *      - wait() releases the object's monitor.
     *      - The Producer suspends until it can resume.
     *      - The Consumer can acquire the monitor and process the existing
     *        order while the Producer is waiting.
     *
     *   5. Once the buffer is empty, store the incoming order ID.
     *
     *   6. Set available = true to indicate that the buffer is full.
     *
     *   7. Call notifyAll() to wake threads waiting on this object's monitor.
     *
     *   8. Exit the method and release the monitor.
     *
     * WHY USE WHILE INSTEAD OF IF?
     * After waking up, the Producer must recheck whether the buffer is empty.
     * Waking up does not guarantee that the condition has become true.
     *
     * EXCEPTION:
     * InterruptedException is propagated to the caller if the Producer
     * is interrupted while waiting.
     * =========================================================================
     */
    public synchronized void placeOrder(int id) throws InterruptedException {

        /*
         * If the buffer is full, wait until the Consumer processes the
         * existing order.
         *
         * The condition is checked again whenever the Producer resumes.
         */
        while (available) {

            /*
             * Release the OrderBuffer object's monitor and suspend the
             * Producer so the Consumer can acquire the monitor.
             */
            wait();
        }

        /*
         * The buffer is empty, so store the incoming order ID.
         *
         * For example, if the Producer calls placeOrder(101),
         * the instance variable orderId becomes 101.
         */
        orderId = id;

        /*
         * Mark the buffer as full because an order is now waiting
         * to be processed by the Consumer.
         */
        available = true;

        /*
         * Notify all threads waiting on this object's monitor.
         *
         * For example, a waiting Consumer can wake up and attempt to
         * process the newly placed order.
         *
         * IMPORTANT:
         * notifyAll() does not immediately release the monitor. A notified
         * thread must reacquire the monitor before continuing.
         */
        notifyAll();
    }


    /*
     * =========================================================================
     * METHOD: processOrder()
     * =========================================================================
     *
     * PURPOSE:
     * Retrieves an order from the buffer and returns its order ID.
     *
     * RETURN TYPE:
     * int - The order ID retrieved from the buffer.
     *
     * SYNCHRONIZATION:
     * The synchronized keyword requires the Consumer to acquire this
     * OrderBuffer object's monitor before entering the method.
     *
     * EXECUTION FLOW:
     *
     *   1. Acquire the OrderBuffer object's monitor.
     *
     *   2. Check whether the buffer is empty.
     *
     *   3. If available is false, there is no order to process.
     *      The Consumer must wait.
     *
     *   4. Call wait() while the buffer is empty.
     *      - wait() releases the object's monitor.
     *      - The Consumer suspends until it can resume.
     *      - The Producer can acquire the monitor and place an order.
     *
     *   5. Once an order is available, copy orderId into a local variable.
     *
     *   6. Set available = false to indicate that the buffer is empty.
     *
     *   7. Call notifyAll() to wake threads waiting on this object's monitor.
     *
     *   8. Return the copied order ID.
     *
     * WHY COPY THE ORDER ID?
     * The local variable preserves the value being processed even after
     * the buffer is marked empty and the Producer is allowed to place
     * another order.
     *
     * WHY USE WHILE INSTEAD OF IF?
     * The Consumer must recheck the buffer condition after waking up
     * because the buffer may still be empty when it reacquires the monitor.
     *
     * EXCEPTION:
     * InterruptedException is propagated to the caller if the Consumer
     * is interrupted while waiting.
     * =========================================================================
     */
    public synchronized int processOrder() throws InterruptedException {

        /*
         * If the buffer is empty, the Consumer cannot process an order.
         *
         * Wait until the Producer places an order and the buffer becomes full.
         */
        while (!available) {

            /*
             * Release the OrderBuffer object's monitor and suspend the
             * Consumer so the Producer can acquire the monitor.
             */
            wait();
        }

        /*
         * Copy the stored order ID into a local variable.
         *
         * For example, if orderId is 101, processedOrderId becomes 101.
         */
        int processedOrderId = orderId;

        /*
         * Mark the buffer as empty because the order has been retrieved.
         *
         * The Producer can now place the next order.
         */
        available = false;

        /*
         * Notify all threads waiting on this object's monitor.
         *
         * For example, a waiting Producer can wake up and attempt to
         * place the next order.
         */
        notifyAll();

        /*
         * Return the retrieved order ID to the Consumer.
         */
        return processedOrderId;
    }

}

