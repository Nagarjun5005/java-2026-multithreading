package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer.orderSystem;

/*
 * Class: OrderMain
 *
 * Purpose:
 * Entry point for the Producer-Consumer application.
 *
 * This class creates a shared OrderBuffer, initializes the
 * Producer and Consumer, starts their threads, and waits
 * for both threads to complete.
 *
 * Execution flow:
 * 1. Create a shared OrderBuffer.
 * 2. Create the Producer and Consumer using the same buffer.
 * 3. Create separate threads for the Producer and Consumer.
 * 4. Start both threads.
 * 5. Wait for both threads to finish using join().
 */
public class OrderMain {

    public static void main(String[] args) throws InterruptedException {

        /*
         * Create one shared buffer.
         *
         * This buffer allows the Producer and Consumer
         * to exchange orders safely.
         */
        OrderBuffer orderBuffer = new OrderBuffer();

        /*
         * Create Producer and Consumer objects.
         *
         * Both receive the SAME OrderBuffer instance,
         * which is essential for synchronization.
         */
        OrderProducer producer = new OrderProducer(orderBuffer);
        OrderConsumer consumer = new OrderConsumer(orderBuffer);

        /*
         * Create separate threads for concurrent execution.
         *
         * The thread names help identify each thread
         * when debugging or reading logs.
         */
        Thread thread1 = new Thread(producer, "producer-thread");
        Thread thread2 = new Thread(consumer, "consumer-thread");

        /*
         * Start both threads.
         *
         * start() schedules each thread to execute its
         * run() method independently.
         *
         * The Producer and Consumer can now run concurrently.
         */
        thread1.start();
        thread2.start();

        /*
         * Wait for both threads to complete.
         *
         * join() makes the main thread wait until the
         * specified worker thread finishes execution.
         *
         * This does NOT synchronize the buffer.
         * Synchronization is handled by OrderBuffer.
         */
        thread1.join();
        thread2.join();
    }
}