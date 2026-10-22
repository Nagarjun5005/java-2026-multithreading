package synchronizedDemo.synchronized01.practice.producerconsumer.arraylist;

/**
 * Represents the Consumer in the Producer-Consumer problem.
 *
 * <p>The Consumer retrieves orders from the shared OrderBuffer and processes
 * them. If the buffer is empty, the Consumer waits until the Producer adds
 * an order.</p>
 *
 * <p>This class implements Runnable so that its work can execute in a
 * separate thread.</p>
 */
public class OrderConsumer implements Runnable {

    /**
     * Shared buffer from which the Consumer retrieves orders.
     *
     * <p>The same OrderBuffer instance must be shared with the Producer
     * to allow both threads to communicate through the same buffer.</p>
     */
    private final OrderBuffer orderBuffer;

    /**
     * Creates a Consumer using the specified shared order buffer.
     *
     * @param orderBuffer the buffer from which orders will be consumed
     */
    public OrderConsumer(OrderBuffer orderBuffer) {
        this.orderBuffer = orderBuffer;
    }

    /**
     * Executes the Consumer's order-processing task.
     *
     * <p>The Consumer attempts to process 10 orders. If the buffer is empty,
     * processOrder() blocks until an order becomes available.</p>
     *
     * <p>If the thread is interrupted while waiting, the interrupt status
     * is restored and the Consumer stops processing further orders.</p>
     */
    @Override
    public void run() {

        // Consume 10 orders during normal execution.
        for (int i = 1; i <= 10; i++) {
            try {
                // Retrieve and remove the next order from the buffer.
                int processedOrder = orderBuffer.processOrder();

                // Display the ID of the order actually consumed.
                System.out.println("Processed Order -----> " + processedOrder);

            } catch (InterruptedException e) {

                // Restore the thread's interrupt status.
                Thread.currentThread().interrupt();

                // Stop consuming additional orders.
                break;
            }
        }
    }
}
