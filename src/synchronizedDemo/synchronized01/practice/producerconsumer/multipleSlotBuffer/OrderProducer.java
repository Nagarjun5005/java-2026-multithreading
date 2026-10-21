package synchronizedDemo.synchronized01.practice.producerconsumer.multipleSlotBuffer;

/*
 * Class: OrderProducer
 *
 * Purpose:
 * Produces orders and inserts them into the shared OrderBuffer.
 *
 * This class implements Runnable so the producer task can
 * execute independently on a separate thread.
 *
 * Responsibilities:
 * 1. Generate order IDs from 101 through 110.
 * 2. Insert each order into the shared buffer.
 * 3. Wait if the buffer is full.
 * 4. Print a message after each order is placed.
 * 5. Handle thread interruption gracefully.
 *
 * The OrderBuffer manages synchronization, so this class
 * does not need to call wait() or notifyAll() directly.
 */
public class OrderProducer implements Runnable {

    /*
     * Shared buffer into which the Producer inserts orders.
     *
     * The Producer and Consumer must receive the same
     * OrderBuffer instance to coordinate correctly.
     */
    private OrderBuffer orderBuffer;

    /*
     * Constructor
     *
     * Receives the shared OrderBuffer instance.
     *
     * @param orderBuffer the buffer into which orders are placed
     */
    public OrderProducer(OrderBuffer orderBuffer) {
        this.orderBuffer = orderBuffer;
    }

    /*
     * run()
     *
     * Defines the task executed by the Producer thread.
     *
     * Execution flow:
     * 1. Generate order IDs from 101 through 110.
     * 2. Call placeOrder() to insert each order.
     * 3. If the buffer is full, placeOrder() waits until
     *    the Consumer removes an order.
     * 4. Print a message after the order is inserted.
     *
     * Interruption handling:
     * If InterruptedException occurs, execution leaves the
     * loop and enters the catch block.
     * The interrupted status is then restored.
     */
    @Override
    public void run() {

        try {
            // Produce 10 orders, from 101 through 110.
            for (int i = 101; i <= 110; i++) {

                // Insert the order into the shared buffer.
                orderBuffer.placeOrder(i);

                // Print after the order has been accepted by the buffer.
                System.out.println("Order placed --->" + i);
            }

        } catch (InterruptedException e) {

            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}
