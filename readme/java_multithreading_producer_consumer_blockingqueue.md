# Producer--Consumer Pattern Using `BlockingQueue`

## Overview

This exercise implements the Java Producer--Consumer pattern using
Java's built-in `BlockingQueue`, specifically `ArrayBlockingQueue`.

Previously, we created a custom bounded buffer using an array,
`putIndex`, `takeIndex`, `count`, `synchronized`, `wait()`, and
`notifyAll()`. In this version, `ArrayBlockingQueue` manages the
storage, capacity, thread-safe access, and blocking behavior internally.

## Learning objectives

-   Share one thread-safe queue between Producer and Consumer threads.
-   Use `ArrayBlockingQueue` with a fixed capacity.
-   Insert items using `put()` and retrieve items using `take()`.
-   Understand blocking when a queue is full or empty.
-   Use `Runnable`, `Thread.start()`, and `Thread.join()`.
-   Compare a custom circular buffer with a built-in concurrency
    utility.

## Project structure

``` text
blockingQueue/
├── OrderMain.java
├── OrderProducer.java
└── OrderConsumer.java
```

## How the classes work together

`OrderMain` creates one `ArrayBlockingQueue<Integer>` with capacity `3`
and passes the **same queue instance** to both the Producer and
Consumer.

``` text
                         OrderMain
                             |
                  ArrayBlockingQueue<Integer>
                       capacity = 3
                             |
                    Shared queue instance
                       /             \
                      v               v
                OrderProducer   OrderConsumer
                    put(id)        take()
                      |               |
                      +--- shared ---+
                           queue
```

The Producer inserts order IDs `101` through `110`. The Consumer
retrieves 10 orders from the queue.

## `OrderMain.java`

Responsibilities:

1.  Create the shared bounded queue.
2.  Create the Producer and Consumer, passing the same queue to both.
3.  Create a thread for each task.
4.  Start both threads.
5.  Call `join()` so the main thread waits for both worker threads to
    finish.

``` java
BlockingQueue<Integer> orders = new ArrayBlockingQueue<>(3);

OrderConsumer consumer = new OrderConsumer(orders);
OrderProducer producer = new OrderProducer(orders);

Thread producerThread = new Thread(producer, "producer-thread");
Thread consumerThread = new Thread(consumer, "consumer-thread");

producerThread.start();
consumerThread.start();

producerThread.join();
consumerThread.join();
```

**Important:** Both classes must receive the same queue object. If they
receive different queues, the Producer will insert into one queue while
the Consumer waits on another.

## `OrderProducer.java`

Responsibilities:

-   Implement `Runnable`.
-   Receive the shared queue through the constructor.
-   Produce order IDs from `101` through `110`.
-   Insert each order using `orders.put(i)`.
-   Print each order after it has been accepted by the queue.

``` java
for (int i = 101; i <= 110; i++) {
    orders.put(i);
    System.out.println("Order placed --->" + i);
}
```

### What does `put()` do?

-   If the queue has space, `put()` inserts the item.
-   If the queue is full, `put()` waits until space becomes available.
-   It can throw `InterruptedException` if the thread is interrupted
    while waiting.

## `OrderConsumer.java`

Responsibilities:

-   Implement `Runnable`.
-   Receive the shared queue through the constructor.
-   Retrieve 10 orders using `orders.take()`.
-   Print each consumed order.

``` java
for (int i = 0; i < 10; i++) {
    Integer processedOrder = orders.take();
    System.out.println("Order consumed ---->" + processedOrder);
}
```

### What does `take()` do?

-   If the queue contains an item, `take()` retrieves and removes the
    next item.
-   If the queue is empty, `take()` waits until an item becomes
    available.
-   It can throw `InterruptedException` if the thread is interrupted
    while waiting.

The Consumer loop processes exactly 10 orders if it is not interrupted
before completing.

## `BlockingQueue` and `ArrayBlockingQueue`

`BlockingQueue` is an interface representing a queue that supports
operations which may wait for the queue to become ready.
`ArrayBlockingQueue` is a bounded implementation backed by an array.

``` java
BlockingQueue<Integer> orders = new ArrayBlockingQueue<>(3);
```

-   `BlockingQueue<Integer>` is the interface type.
-   `ArrayBlockingQueue<>(3)` creates the implementation with a capacity
    of three.
-   The queue preserves FIFO (first-in, first-out) ordering.
-   The queue is thread-safe.

Using the interface type makes it easier to change the implementation
later without changing the Producer and Consumer code, provided the
replacement supports the same operations.

## Useful queue operations

  -----------------------------------------------------------------------
  Method                              Behavior
  ----------------------------------- -----------------------------------
  `put(item)`                         Inserts an item, waiting if the
                                      queue is full

  `take()`                            Removes and returns the next item,
                                      waiting if the queue is empty

  `offer(item)`                       Attempts to insert immediately;
                                      returns `false` if no capacity is
                                      available

  `poll()`                            Attempts to retrieve immediately;
                                      returns `null` if the queue is
                                      empty
  -----------------------------------------------------------------------

For this exercise, `put()` and `take()` are appropriate because the
Producer and Consumer should wait rather than drop work or receive
`null` when the queue is temporarily unavailable.

## Example of blocking behavior

The queue has a capacity of three.

  Step   Operation                      Queue contents        Size
  ------ ------------------------------ ------------------- ------
  1      Producer inserts `101`         `[101]`                  1
  2      Producer inserts `102`         `[101, 102]`             2
  3      Producer inserts `103`         `[101, 102, 103]`        3
  4      Producer attempts `put(104)`   `[101, 102, 103]`        3
  5      Consumer takes `101`           `[102, 103]`             2
  6      Producer can insert `104`      `[102, 103, 104]`        3

This is one possible execution order, not a guaranteed schedule. The JVM
decides how the threads are scheduled.

## Interruption handling

Both `put()` and `take()` can throw `InterruptedException` when a thread
is interrupted while blocked.

The exercise uses:

``` java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

Restoring the interrupt status preserves the interruption signal. Since
the catch surrounds the loop, the task stops processing further items
after an interruption.

## What `join()` does---and does not do

``` java
producerThread.join();
consumerThread.join();
```

`join()` makes the calling thread (`main`) wait for the specified thread
to terminate. It does **not** manage queue capacity or coordinate
Producer and Consumer access to the queue; `BlockingQueue` handles that
coordination.

## Comparison with the custom circular buffer

  -----------------------------------------------------------------------
  Responsibility          Custom circular buffer  `ArrayBlockingQueue`
  ----------------------- ----------------------- -----------------------
  Storage                 Manually managed array  Internal array

  Capacity tracking       Manual `count`          Managed internally

  Index management        Manual `putIndex` and   Managed internally
                          `takeIndex`             

  Waiting when full       `wait()`                `put()`

  Waiting when empty      `wait()`                `take()`

  Thread coordination     `notifyAll()`           Managed internally

  Main learning focus     Understand low-level    Use a standard
                          synchronization         concurrency abstraction
  -----------------------------------------------------------------------

In the standard OpenJDK implementation, `ArrayBlockingQueue` uses a
`ReentrantLock` and `Condition` objects internally. This is an
implementation detail; application code should generally use the public
`BlockingQueue` API rather than depend on internal locking details. Not
every `BlockingQueue` implementation uses the same synchronization
mechanism.

## Interview takeaways

Be ready to explain:

1.  Why must the Producer and Consumer share the same queue instance?
2.  What is the difference between `put()` and `offer()`?
3.  What is the difference between `take()` and `poll()`?
4.  What happens when a bounded queue becomes full?
5.  What happens when the queue is empty?
6.  Why is `ArrayBlockingQueue` suitable for a bounded
    Producer--Consumer problem?
7.  What does `join()` do?
8.  How does this approach differ from a buffer implemented with
    `synchronized`, `wait()`, and `notifyAll()`?
9.  Why is interruption handling important?
10. How would the design change with multiple Producers and Consumers?

## Possible improvements

This is a learning exercise with one Producer and one Consumer. Both
loops expect exactly 10 orders.

In a production-style design, consider:

-   How Consumers should shut down when no more work will arrive.
-   How to handle failures during order processing.
-   Whether multiple Producers or Consumers are needed.
-   Whether `ExecutorService` should manage worker threads.
-   Whether queue capacity should be configurable.
-   How to monitor queue depth and processing delays.

**Important:** `BlockingQueue` does not automatically signal that a
Producer has finished. If a Consumer calls `take()` after all available
items have been consumed, it will wait for another item unless it is
interrupted or the design uses a shutdown protocol.

## Summary

`ArrayBlockingQueue` provides a thread-safe, bounded queue with blocking
insertion and retrieval operations.

Instead of implementing array indexes, counters, and low-level thread
coordination ourselves, we use:

``` java
orders.put(orderId);          // Producer
Integer order = orders.take(); // Consumer
```

Understanding the custom circular buffer explains the underlying
concurrency problem. Understanding `BlockingQueue` shows how to solve
that problem using a standard Java API.
