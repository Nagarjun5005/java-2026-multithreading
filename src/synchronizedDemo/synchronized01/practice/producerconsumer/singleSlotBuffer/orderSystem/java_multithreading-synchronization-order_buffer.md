# Producer–Consumer Problem: Single-Slot Order Buffer

## 1. Problem Statement

Implement a Java Producer–Consumer application using a **single-slot buffer**.

An `OrderProducer` produces three orders with IDs `101`, `102`, and `103`. An `OrderConsumer` retrieves and processes those orders. Both threads share the same `OrderBuffer` object.

The buffer can hold **only one unprocessed order at a time**.

The application must ensure that:
- The Producer cannot place a new order while the buffer is full.
- The Consumer cannot process an order while the buffer is empty.
- Both threads coordinate safely without overwriting or consuming unavailable data.
- Both threads finish after all three orders have been produced and processed.

## 2. Main Components

| Class | Responsibility |
|---|---|
| `OrderBuffer` | Stores one order and coordinates access between the threads. |
| `OrderProducer` | Produces order IDs `101`, `102`, and `103`. |
| `OrderConsumer` | Processes exactly three orders from the buffer. |
| `OrderMain` | Creates the shared buffer, starts both threads, and waits for them to finish. |

## 3. Buffer State

The buffer uses two fields:
- `orderId`: the order ID currently stored in the buffer.
- `available`: indicates whether an order is waiting to be consumed.

| `available` value | Buffer state | Producer | Consumer |
|---|---|---|---|
| `false` | Empty | May place an order | Must wait |
| `true` | Full | Must wait | May process the order |

**Remember:** `available` means “an order is available for the Consumer,” not “the Producer is available.”

The buffer starts with `available = false`.

## 4. How Synchronization Works

### `synchronized`

Both `placeOrder()` and `processOrder()` are synchronized instance methods. They use the same `OrderBuffer` object's monitor (intrinsic lock).

Only one thread at a time can execute either synchronized method on that same object. This protects changes to `orderId` and `available`.

### Producer: `placeOrder(int id)`

1. Enter the synchronized method by acquiring the buffer's monitor.
2. Check `while (available)`.
3. If the buffer is full, call `wait()`.
   - `wait()` releases the buffer's monitor.
   - The Producer waits until it can resume.
   - Releasing the monitor lets the Consumer enter `processOrder()`.
4. When the buffer is empty, store the supplied order ID.
5. Set `available = true` to mark the buffer full.
6. Call `notifyAll()` to wake threads waiting on this buffer's monitor.
7. Exit the method and release the monitor.

### Consumer: `processOrder()`

1. Enter the synchronized method by acquiring the buffer's monitor.
2. Check `while (!available)`.
3. If the buffer is empty, call `wait()`.
   - `wait()` releases the buffer's monitor.
   - The Consumer waits until it can resume.
   - Releasing the monitor lets the Producer enter `placeOrder()`.
4. Copy the stored `orderId` into a local variable.
5. Set `available = false` to mark the buffer empty.
6. Call `notifyAll()` to wake threads waiting on this buffer's monitor.
7. Return the copied order ID and release the monitor when the method exits.

### Why use `while`, not `if`?

A thread must recheck the condition after waking up. It may wake up without the condition being satisfied, or another thread may change the buffer state before it reacquires the monitor. The `while` loop checks the condition again before proceeding.

### Important note about `notifyAll()`

`notifyAll()` wakes threads waiting on the same object's monitor, but it does **not** immediately hand over the monitor. A notified thread must reacquire the monitor before continuing. It then rechecks its `while` condition.

## 5. Synchronization Sketch

```text
                     SAME OrderBuffer OBJECT
                 +-----------------------------+
                 | orderId                     |
                 | available (true / false)    |
                 +-----------------------------+
                       ^                 |
                       |                 |
             placeOrder(id)       processOrder()
                       |                 |
                OrderProducer       OrderConsumer
                       |                 |
             while (available)   while (!available)
                       |                 |
                    wait()             wait()
                       |                 |
            Buffer full:           Buffer empty:
            Producer waits         Consumer waits
                       |                 |
             Consumer consumes    Producer produces
             available = false    available = true
                       |                 |
                   notifyAll()       notifyAll()
                       |                 |
                 Recheck condition after waking
```

## 6. Order-by-Order Walkthrough

The buffer can hold one order, so the Producer cannot place the next order until the current one has been consumed.

| Step | Producer action | Buffer state | Consumer action |
|---|---|---|---|
| 1 | Starts with order `101` | Empty (`false`) | May wait if it runs first |
| 2 | Places `101`; sets `available = true` | Full (`true`) | Can now consume `101` |
| 3 | Attempts to place `102` | If still full, Producer waits | Consumes `101`; sets `available = false` |
| 4 | Places `102`; sets `available = true` | Full (`true`) | Consumes `102`; sets `available = false` |
| 5 | Places `103`; sets `available = true` | Full (`true`) | Consumes `103`; sets `available = false` |
| 6 | Producer finishes | Empty (`false`) after final consumption | Consumer finishes |

The exact thread scheduling and print-message interleaving can vary. The buffer conditions ensure that each order is placed before it is consumed.

## 7. Sequence Diagram

```text
OrderMain          Producer             OrderBuffer             Consumer
   |                   |                     |                      |
   |-- start threads ->|                     |                      |
   |                   |-- placeOrder(101) ->|                      |
   |                   |                     | available = true     |
   |                   |                     |<-- processOrder() ---|
   |                   |                     | return 101           |
   |                   |                     | available = false    |
   |                   |-- placeOrder(102) ->|                      |
   |                   |                     | available = true     |
   |                   |                     |<-- processOrder() ---|
   |                   |                     | return 102           |
   |                   |-- placeOrder(103) ->|                      |
   |                   |                     | available = true     |
   |                   |                     |<-- processOrder() ---|
   |                   |                     | return 103           |
   |<------ join() waits for both worker threads to finish ---------|
```

This is a conceptual sequence: the actual scheduler may interleave operations differently. If one side reaches the buffer too early, it waits until the state allows it to proceed.

## 8. Expected Result

The Consumer should process all three order IDs:

```text
Processed Order--->101
Processed Order--->102
Processed Order--->103
```

Other log messages may appear in a different order depending on scheduling.

## 9. Role of `start()` and `join()`

- `start()` asks the JVM to schedule a new thread and execute its `run()` method.
- Calling `run()` directly would not start a separate thread.
- `join()` makes the main thread wait until the specified worker thread terminates.
- `join()` does not synchronize access to the buffer; `synchronized`, `wait()`, and `notifyAll()` handle buffer coordination.

## 10. Key Takeaways

1. **Empty → Produce.** `available = false` means the Producer may place an order.
2. **Full → Consume.** `available = true` means the Consumer may process the order.
3. **`synchronized` protects shared state** by controlling access through the same object's monitor.
4. **`wait()` releases the monitor** and suspends a thread while it cannot proceed.
5. **`notifyAll()` wakes waiting threads**, which must reacquire the monitor and recheck their conditions.
6. **A loop of three orders does not mean three orders are stored at once.** The single-slot buffer holds only one unprocessed order at a time.
7. The Producer and Consumer must receive the **same `OrderBuffer` instance** for this synchronization to coordinate correctly.
