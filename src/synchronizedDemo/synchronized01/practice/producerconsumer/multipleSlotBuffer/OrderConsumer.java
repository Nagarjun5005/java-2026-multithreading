package synchronizedDemo.synchronized01.practice.producerconsumer.multipleSlotBuffer;

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
 * 1. Retrieve orders from the shared buffer.
 * 2. Wait if the buffer is empty.
 * 3. Process exactly 10 orders.
 * 4. Print each consumed order ID.
 * 5. Handle thread interruption gracefully.
 *
 * The OrderBuffer handles synchronization, so this class
 * does not need to manage wait() or notifyAll() directly.
 */
public class OrderConsumer implements Runnable {

    /*
     * Shared buffer from which the Consumer retrieves orders.
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
     * 1. Repeat the consumption process 10 times.
     * 2. Call processOrder() to retrieve the next order.
     * 3. If the buffer is empty, processOrder() waits until
     *    the Producer places an order.
     * 4. Print the retrieved order ID.
     *
     * Interruption handling:
     * If InterruptedException occurs, execution leaves the
     * loop and enters the catch block.
     * The interrupted status is then restored.
     */
    @Override
    public void run() {

        try {
            // Consume exactly 10 orders.
            for (int i = 0; i < 10; i++) {

                // Retrieve the next order from the shared buffer.
                int processedOrder = orderBuffer.processOrder();

                // Print the ID of the consumed order.
                System.out.println("Order consumed ---->" + processedOrder);
            }

        } catch (InterruptedException e) {

            // Restore the interrupted status of this thread.
            Thread.currentThread().interrupt();
        }
    }
}
