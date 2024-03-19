package queue.test;

import queue.ArrayQueue;

public class ArrayQueueTest {
    public static void fill(ArrayQueue queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(prefix + i);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.dequeue());
        }
    }

    public static void main(String[] args) {

        ArrayQueue stack1 = new ArrayQueue();
        System.out.println(stack1.size());
        stack1.enqueue(1);
        stack1.enqueue(2);
        System.out.println(stack1.size());
        stack1.clear();
        System.out.println(stack1.size());

    }
}

