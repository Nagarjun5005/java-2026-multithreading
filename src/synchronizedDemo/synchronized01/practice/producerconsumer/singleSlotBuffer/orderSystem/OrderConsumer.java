package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer.orderSystem;

/*
 * Class: OrderConsumer
 *
 * Purpose:
 * Consumes orders from the shared OrderBuffer.
 *
 * This class implements Runnable so the consumer task can
 * execute independently on a separate thread.
 *
 * Responsibilities:
 * 1. Retrieve orders from the shared OrderBuffer.
 * 2. Wait if the buffer is empty.
 * 3. Process three orders.
 * 4. Handle thread interruption gracefully.
 */
public class OrderConsumer implements Runnable {

    /*
     * Shared buffer used to retrieve orders produced
     * by the OrderProducer thread.
     *
     * Both Producer and Consumer must use the same
     * OrderBuffer instance.
     */
    private OrderBuffer orderBuffer;

    /*
     * Constructor
     *
     * Receives the shared OrderBuffer instance.
     *
     * @param orderBuffer the buffer from which orders are consumed
     */
    public OrderConsumer(OrderBuffer orderBuffer) {
        this.orderBuffer = orderBuffer;
    }

    /*
     * run()
     *
     * Defines the task executed by the Consumer thread.
     *
     * Execution flow:
     * 1. Repeat the consumption process three times.
     * 2. Call processOrder() to retrieve an order.
     * 3. If the buffer is empty, processOrder() waits
     *    until the Producer places an order.
     * 4. Print the ID of the processed order.
     *
     * If the thread is interrupted while waiting, restore
     * its interrupted status and stop consuming orders.
     */
    @Override
    public void run() {
        try {
            for (int i = 0; i < 3; i++) {

                // Retrieve an order from the shared buffer.
                int processOrder = orderBuffer.processOrder();

                // Print the ID of the consumed order.
                System.out.println("Processed Order--->" + processOrder);
            }

        } catch (InterruptedException e) {

            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}
