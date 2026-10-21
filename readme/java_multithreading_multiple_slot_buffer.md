# Producer–Consumer with a Multiple-Slot Circular Buffer

## 1. Overview

This project demonstrates the **Producer–Consumer problem** in Java using:

- A fixed-size array with a capacity of **3 orders**
- Two concurrent threads: a Producer and a Consumer
- `synchronized` methods to protect shared state
- `wait()` to pause a thread when it cannot proceed
- `notifyAll()` to wake waiting threads after the buffer changes
- Circular indexing (`% capacity`) to reuse array positions
- FIFO (First In, First Out) order processing

The Producer creates order IDs from `101` through `110`. The Consumer retrieves and processes exactly 10 orders.

The central idea is that the Producer and Consumer can run concurrently, while access to their shared buffer is coordinated safely.

---

## 2. Project structure

All four classes belong to:

```text
synchronizedDemo.synchronized01.practice.producerconsumer.multipleSlotBuffer
```

```text
multipleSlotBuffer/
├── OrderBuffer.java
├── OrderProducer.java
├── OrderConsumer.java
└── OrderMain.java
```

### Responsibilities

| Class | Responsibility |
|---|---|
| `OrderBuffer` | Stores orders and coordinates Producer/Consumer access |
| `OrderProducer` | Produces order IDs `101`–`110` |
| `OrderConsumer` | Consumes 10 orders |
| `OrderMain` | Creates the shared buffer, starts threads, and waits for them to finish |

---

## 3. How the buffer is represented

The buffer is an array:

```java
private final int capacity = 3;
private int[] orders = new int[capacity];
```

Initially, Java initializes an `int[]` with zeros:

```text
Index:     0      1      2
        +------+------+------+
        |   0  |   0  |   0  |
        +------+------+------+

putIndex  = 0
takeIndex = 0
count     = 0
```

The zeros do **not** mean there are three orders in the buffer. The `count` variable tells us how many orders are logically stored.

### Buffer state variables

| Variable | Meaning |
|---|---|
| `orders` | Array that stores order IDs |
| `capacity` | Maximum number of orders the buffer can hold: `3` |
| `putIndex` | Array position for the next insertion |
| `takeIndex` | Array position for the next removal |
| `count` | Number of orders currently waiting in the buffer |

The key conditions are:

- `count == 0`: buffer is empty; the Consumer must wait.
- `count == capacity`: buffer is full; the Producer must wait.
- Otherwise: the buffer has at least one order and at least one free slot.

---

## 4. How `placeOrder()` works

The Producer calls:

```java
orderBuffer.placeOrder(id);
```

The method follows this sequence:

1. Acquire the `OrderBuffer` object's monitor by entering the synchronized method.
2. If the buffer is full (`count == capacity`), call `wait()`.
3. Insert the order at `orders[putIndex]`.
4. Advance `putIndex` using circular indexing.
5. Increment `count`.
6. Call `notifyAll()` to wake threads waiting on this buffer.
7. Exit the synchronized method and release the monitor.

The essential logic is:

```java
while (count == capacity) {
    wait();
}

orders[putIndex] = id;
putIndex = (putIndex + 1) % capacity;
count++;
notifyAll();
```

### Why does the Producer wait when the buffer is full?

Imagine the buffer contains three unprocessed orders:

```text
orders = [101, 102, 103]
count  = 3
```

The Producer attempts to insert `104`. Since `count == capacity`, it must wait instead of overwriting an unprocessed order.

When the Consumer removes an order, it decreases `count` and calls `notifyAll()`. The Producer can then wake, reacquire the monitor, recheck the condition, and insert the next order if space is available.

---

## 5. How `processOrder()` works

The Consumer calls:

```java
int processedOrder = orderBuffer.processOrder();
```

The method follows this sequence:

1. Acquire the same `OrderBuffer` object's monitor.
2. If the buffer is empty (`count == 0`), call `wait()`.
3. Read the order at `orders[takeIndex]`.
4. Advance `takeIndex` using circular indexing.
5. Decrement `count`.
6. Call `notifyAll()` to wake threads waiting on this buffer.
7. Return the retrieved order ID.

The essential logic is:

```java
while (count == 0) {
    wait();
}

int processedOrder = orders[takeIndex];
takeIndex = (takeIndex + 1) % capacity;
count--;
notifyAll();

return processedOrder;
```

### Why doesn't the Consumer wait for all three slots to fill?

It only waits when `count == 0`. If even one order is available, it can consume that order.

For example:

```text
orders = [101, 0, 0]
count  = 1
```

The Consumer can immediately retrieve `101`. The buffer's capacity is its **maximum** size, not the number of orders required before consumption can begin.

The array may retain the old value in a slot after it has been consumed. That is okay: `count`, `putIndex`, and `takeIndex` track the buffer's logical contents.

---

## 6. What `synchronized` guarantees

Both buffer methods are declared `synchronized`:

```java
public synchronized void placeOrder(int id) throws InterruptedException
```

```java
public synchronized int processOrder() throws InterruptedException
```

Because both methods are synchronized on the **same `OrderBuffer` instance**, only one thread at a time can execute either method while holding that object's monitor.

This protects shared state such as:

- `orders`
- `putIndex`
- `takeIndex`
- `count`

Without synchronization, the Producer and Consumer could modify these fields concurrently and leave the buffer in an inconsistent state.

**Important:** Synchronization does not mean the Producer must finish before the Consumer starts. It means the operations that access the shared buffer are coordinated safely.

---

## 7. How `wait()` and `notifyAll()` work together

### `wait()`

Calling `wait()`:

- Must occur while the thread owns that object's monitor.
- Releases that object's monitor while the thread waits.
- Allows another thread to enter a synchronized method on the same object.
- Throws `InterruptedException` if the waiting thread is interrupted.

For example, when the buffer is full, the Producer calls `wait()`. This releases the monitor so the Consumer can enter `processOrder()` and remove an order.

### `notifyAll()`

Calling `notifyAll()` wakes threads waiting on that same object's monitor. It does **not** immediately transfer the monitor to a waiting thread. A woken thread must reacquire the monitor before continuing.

After waking, the thread rechecks its condition.

### Why use `while`, not `if`?

Use:

```java
while (count == capacity) {
    wait();
}
```

and:

```java
while (count == 0) {
    wait();
}
```

Do not use `if` for these checks. A thread can wake when its condition is still false, including after a spurious wakeup or because another thread changed the state first. The `while` loop ensures the condition is checked again after waking.

---

## 8. Why the index uses modulo (`%`)

The buffer is circular. Once an index reaches the last array position, the next index wraps around to zero.

```java
putIndex = (putIndex + 1) % capacity;
takeIndex = (takeIndex + 1) % capacity;
```

With capacity `3`:

| Current index | Calculation | Next index |
|---:|---|---:|
| 0 | `(0 + 1) % 3` | 1 |
| 1 | `(1 + 1) % 3` | 2 |
| 2 | `(2 + 1) % 3` | 0 |

The array does not shift its elements. The indexes move around the array, allowing freed slots to be reused.

---

## 9. Example execution

The Producer creates orders `101` through `110`; the Consumer retrieves 10 orders. One possible sequence is shown below.

### Step 1: Producer places orders

```text
Place 101  -> [101,   _,   _]  count = 1
Place 102  -> [101, 102,   _]  count = 2
Place 103  -> [101, 102, 103]  count = 3
```

The buffer is full. If the Producer tries to place `104` before the Consumer removes an order, it waits.

### Step 2: Consumer removes the first order

The Consumer retrieves `101`:

```text
Retrieved order = 101
takeIndex = 1
count = 2
```

The Producer can now resume and insert another order when it reacquires the monitor.

The old `101` may still physically appear in array index `0`; logically, it is no longer in the buffer.

### Step 3: The process continues

The Producer and Consumer continue coordinating until all ten orders have been inserted and retrieved.

The Consumer must retrieve the IDs in FIFO order:

```text
101, 102, 103, 104, 105, 106, 107, 108, 109, 110
```

The exact timing of Producer and Consumer print statements can vary because the threads run concurrently. For example, the Consumer's print statement can appear before the Producer's print statement even though the insertion happened first: the Consumer may run after `placeOrder()` releases the monitor but before the Producer prints its message.

Judge correctness by the order IDs and buffer invariants, not by requiring a particular interleaving of log messages.

---

## 10. How the two threads are started

`OrderMain` creates a single buffer and passes that same instance to both objects:

```java
OrderBuffer orderBuffer = new OrderBuffer();

OrderProducer producer = new OrderProducer(orderBuffer);
OrderConsumer consumer = new OrderConsumer(orderBuffer);
```

It then starts the threads:

```java
Thread producerThread = new Thread(producer, "producer-thread");
Thread consumerThread = new Thread(consumer, "consumer-thread");

producerThread.start();
consumerThread.start();
```

Calling `start()` lets each task execute on its own thread.

Finally:

```java
producerThread.join();
consumerThread.join();
```

`join()` makes the main thread wait for the worker threads to finish. It does **not** synchronize the buffer; `synchronized`, `wait()`, and `notifyAll()` handle buffer coordination.

---

## 11. Interruption handling

The Producer and Consumer catch `InterruptedException` and restore the interrupted status:

```java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

Because the `try-catch` surrounds the whole loop, an interruption exits the loop and stops that task from processing further orders.

In this exercise, the Producer and Consumer are configured for 10 orders each. If either thread is interrupted before completing all ten operations, the other thread may remain waiting because the remaining work will not be completed. More robust production designs should define cancellation and shutdown behaviour explicitly.

---

## 12. Checklist for testing

After running `OrderMain`, verify:

- [ ] The Producer attempts to place orders `101` through `110`.
- [ ] The Consumer processes exactly 10 orders during a normal, uninterrupted run.
- [ ] Consumed IDs appear in FIFO order, from `101` through `110`.
- [ ] No order is lost or processed twice.
- [ ] `count` never goes below `0` or above `capacity`.
- [ ] The Producer waits when the buffer is full.
- [ ] The Consumer waits when the buffer is empty.
- [ ] Circular indexes wrap from `2` to `0`.
- [ ] Both threads terminate, and `join()` allows the main thread to finish.

## 13. Key takeaways

1. **The array stores values; `count` tracks how many are logically queued.**
2. **`putIndex` and `takeIndex` implement FIFO circular indexing.**
3. **`synchronized` protects shared buffer state using one object's monitor.**
4. **`wait()` releases that monitor while the calling thread waits.**
5. **`notifyAll()` wakes waiting threads, which must reacquire the monitor and recheck their conditions.**
6. **A multiple-slot buffer can hold up to three orders; the Consumer can consume as soon as one order is available.**
7. **Concurrent execution does not imply a fixed ordering of log messages.**

This exercise demonstrates how to implement Producer–Consumer coordination manually. In real applications, Java's `BlockingQueue` (for example, `ArrayBlockingQueue`) often provides a simpler and less error-prone way to implement the same pattern.
