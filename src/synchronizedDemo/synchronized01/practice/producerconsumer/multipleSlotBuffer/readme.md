# Problem Statement: Producer–Consumer with a Fixed-Size Array Buffer

## 1. Objective

Implement a Producer–Consumer system in Java using a fixed-size array that can store multiple orders simultaneously.

You have already implemented a single-slot buffer using `synchronized`, `wait()`, and `notifyAll()`.

Now, extend that concept to support a buffer with a capacity of **3 orders**.

The Producer generates orders, and the Consumer processes them. Both threads share the same buffer.

## 2. Functional Requirements

### A. OrderBuffer class

Create a class named `OrderBuffer` with the following fields:

| Field       | Type    | Purpose                                            |
| ----------- | ------- | -------------------------------------------------- |
| `orders`    | `int[]` | Stores order IDs                                   |
| `capacity`  | `int`   | Maximum number of orders the buffer can hold       |
| `putIndex`  | `int`   | Position where the next order will be inserted     |
| `takeIndex` | `int`   | Position from which the next order will be removed |
| `count`     | `int`   | Number of orders currently in the buffer           |

**Initial conditions:**

* Buffer capacity: 3
* `putIndex = 0`
* `takeIndex = 0`
* `count = 0`

### B. placeOrder(int id)

Implement a method that allows the Producer to place an order in the buffer.

Requirements:

1. The method must be synchronized.
2. If the buffer is full, the Producer must wait.
3. Insert the order at `putIndex`.
4. Update `putIndex` so the next order goes to the correct position.
5. Increment `count`.
6. Notify waiting threads after inserting the order.

### C. processOrder()

Implement a method that allows the Consumer to retrieve an order.

Requirements:

1. The method must be synchronized.
2. If the buffer is empty, the Consumer must wait.
3. Retrieve the order at `takeIndex`.
4. Update `takeIndex` so the next order is retrieved from the correct position.
5. Decrement `count`.
6. Notify waiting threads after removing the order.
7. Return the retrieved order ID.

### D. Circular Buffer Behaviour

Your array must behave like a circular buffer.

For example, with a capacity of 3:

Initially:

`[101, 102, 103]`

After removing `101`, the next insertion should reuse the freed position instead of requiring the array to shift its elements.

Use the modulo operator (`%`) to wrap the indices back to zero when they reach the end of the array.

### E. OrderProducer class

Create a class implementing `Runnable`.

Requirements:

* Receive the shared `OrderBuffer` through its constructor.
* Generate order IDs from `101` through `110`.
* Place each order into the buffer using `placeOrder()`.
* Print a message when each order is placed.
* Handle `InterruptedException` appropriately.

### F. OrderConsumer class

Create a class implementing `Runnable`.

Requirements:

* Receive the same `OrderBuffer` instance through its constructor.
* Consume exactly 10 orders.
* Retrieve each order using `processOrder()`.
* Print the ID of each processed order.
* Handle `InterruptedException` appropriately.

### G. OrderMain class

Create the application entry point.

Requirements:

1. Create one `OrderBuffer` with capacity 3.
2. Create one Producer and one Consumer.
3. Pass the same buffer instance to both.
4. Create separate threads named `producer-thread` and `consumer-thread`.
5. Start both threads.
6. Use `join()` to wait for both threads to finish.

## 3. Concurrency Rules

Your implementation must follow these rules:

* Use `synchronized` to protect the shared buffer.
* Use `while`, not `if`, when checking conditions before calling `wait()`.
* The Producer must wait when the buffer is full.
* The Consumer must wait when the buffer is empty.
* Use `notifyAll()` after modifying the buffer.
* Never allow `count` to exceed 3 or fall below 0.
* Maintain FIFO ordering: orders must be processed in the order they were placed.

## 4. Expected Behaviour

Your program should:

* Produce 10 orders, from `101` through `110`.
* Process all 10 orders exactly once.
* Never lose or duplicate an order.
* Never process an order before it has been placed.
* Correctly reuse array positions when the indices wrap around.
* Terminate without getting stuck waiting indefinitely.

The exact interleaving of Producer and Consumer log messages may vary.

## 5. Implementation Order

Implement the classes in this order:

1. `OrderBuffer` — implement the circular buffer and synchronization.
2. `OrderProducer` — produce 10 orders.
3. `OrderConsumer` — consume 10 orders.
4. `OrderMain` — create and start the threads.

**Start with `OrderBuffer` only.** Once you've implemented `placeOrder()` and `processOrder()`, send me your code. I'll review it and explain any mistakes without giving you the complete solution upfront.
