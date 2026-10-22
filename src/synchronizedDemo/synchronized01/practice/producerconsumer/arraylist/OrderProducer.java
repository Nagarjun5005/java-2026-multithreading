package synchronizedDemo.synchronized01.practice.producerconsumer.arraylist;

/**
 * Represents the Producer in the Producer-Consumer problem.
 *
 * <p>The Producer generates orders and adds them to the shared OrderBuffer.
 * If the buffer is full, the Producer waits until the Consumer removes
 * an order and makes space available.</p>
 *
 * <p>This class implements Runnable so that order production can execute
 * in a separate thread.</p>
 */
public class OrderProducer implements Runnable {

    /**
     * Shared buffer into which the Producer places orders.
     *
     * <p>The same OrderBuffer instance must be shared with the Consumer
     * so that both threads communicate through the same buffer.</p>
     */
    private final OrderBuffer orderBuffer;

    /**
     * Creates a Producer using the specified shared order buffer.
     *
     * @param orderBuffer the buffer into which orders will be placed
     */
    public OrderProducer(OrderBuffer orderBuffer) {
        this.orderBuffer = orderBuffer;
    }

    /**
     * Executes the Producer's order-generation task.
     *
     * <p>The Producer generates 10 orders, with IDs ranging from 1 to 10,
     * and adds them to the buffer.</p>
     *
     * <p>If the buffer is full, placeOrder() blocks until space becomes
     * available. If the thread is interrupted while waiting, the interrupt
     * status is restored and the Producer stops producing further orders.</p>
     */
    @Override
    public void run() {

        // Produce 10 orders with IDs from 1 through 10.
        for (int i = 1; i <= 10; i++) {
            try {
                // Add the order to the shared buffer.
                orderBuffer.placeOrder(i);

                // Display the ID of the order successfully placed.
                System.out.println("Order Placed -----> " + i);

            } catch (InterruptedException e) {

                // Restore the thread's interrupt status.
                Thread.currentThread().interrupt();

                // Stop producing additional orders.
                break;
            }
        }
    }
}
