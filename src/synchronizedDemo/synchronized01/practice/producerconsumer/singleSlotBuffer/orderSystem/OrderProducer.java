package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer.orderSystem;

/*
 * Class: OrderProducer
 *
 * Purpose:
 * Produces orders and places them into the shared OrderBuffer.
 *
 * This class implements Runnable so its task can execute
 * independently on a separate thread.
 *
 * Responsibilities:
 * 1. Generate order IDs from 101 to 103.
 * 2. Place each order into the shared OrderBuffer.
 * 3. Wait automatically if the buffer already contains an order.
 * 4. Handle interruption gracefully.
 */
public class OrderProducer implements Runnable {

    /*
     * Shared buffer used to exchange orders between
     * the Producer and Consumer threads.
     *
     * Both threads must use the same OrderBuffer instance
     * for synchronization to work correctly.
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
     * 1. Generate order IDs from 101 through 103.
     * 2. Call placeOrder() to place each order in the buffer.
     * 3. If the buffer is full, placeOrder() waits until
     *    the Consumer processes the existing order.
     * 4. Print a message after the order is placed.
     *
     * If the thread is interrupted while waiting, restore
     * its interrupted status and stop producing orders.
     */
    @Override
    public void run() {
        try {
            for (int i = 101; i <= 103; i++) {

                // Place the order in the shared buffer.
                orderBuffer.placeOrder(i);

                // Print after the order has been placed.
                System.out.println("Order placed --->" + i);
            }

        } catch (InterruptedException e) {

            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}