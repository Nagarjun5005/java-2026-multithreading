package synchronizedDemo.synchronized01.practice.producerconsumer.multipleSlotBuffer;

/*
 * Class: OrderMain
 *
 * Purpose:
 * Entry point for the multiple-slot Producer-Consumer application.
 *
 * This class initializes the shared OrderBuffer, creates the
 * Producer and Consumer objects, starts their threads, and
 * waits for both threads to complete.
 *
 * Execution flow:
 * 1. Create a shared OrderBuffer with capacity 3.
 * 2. Create the Producer and Consumer using the same buffer.
 * 3. Create separate threads for the Producer and Consumer.
 * 4. Start both threads.
 * 5. Wait for both threads to finish using join().
 */
public class OrderMain {

    public static void main(String[] args) throws InterruptedException {

        /*
         * Create a single shared buffer.
         *
         * The buffer can hold up to three unprocessed orders.
         */
        OrderBuffer orderBuffer = new OrderBuffer();

        /*
         * Create the Producer and Consumer.
         *
         * Both objects receive the same OrderBuffer instance,
         * allowing them to exchange orders safely.
         */
        OrderProducer producer = new OrderProducer(orderBuffer);
        OrderConsumer consumer = new OrderConsumer(orderBuffer);

        /*
         * Create separate threads for concurrent execution.
         *
         * Thread names help identify each thread in logs
         * and during debugging.
         */
        Thread producerThread = new Thread(producer, "producer-thread");
        Thread consumerThread = new Thread(consumer, "consumer-thread");

        /*
         * Start both threads.
         *
         * start() schedules each thread to execute its run()
         * method independently.
         */
        producerThread.start();
        consumerThread.start();

        /*
         * Wait for both threads to finish.
         *
         * join() makes the main thread wait until the
         * corresponding worker thread terminates.
         *
         * Synchronization of the shared buffer is handled
         * by OrderBuffer, not by join().
         */
        producerThread.join();
        consumerThread.join();
    }
}
