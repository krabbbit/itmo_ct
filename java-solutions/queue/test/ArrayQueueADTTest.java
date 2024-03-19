package queue.test;

import queue.ArrayQueueADT;

public class ArrayQueueADTTest {
    public static void fill(ArrayQueueADT stack, String prefix) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(stack, prefix + i);
        }
    }

    public static void dump(ArrayQueueADT stack) {
        while (!ArrayQueueADT.isEmpty(stack)) {
            System.out.println(
                    ArrayQueueADT.size(stack) + " " +
                            ArrayQueueADT.element(stack) + " " +
                            ArrayQueueADT.dequeue(stack)
            );
        }
    }

    public static void main(String[] args) {
        ArrayQueueADT stack1 = new ArrayQueueADT();
        ArrayQueueADT.enqueue(stack1,1);
        ArrayQueueADT stack2 = new ArrayQueueADT();
        fill(stack1, "s1_");
        fill(stack2, "s2_");
        dump(stack1);
        dump(stack2);
    }
}
