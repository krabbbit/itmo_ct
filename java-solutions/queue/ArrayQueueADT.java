package queue;


import java.util.function.Predicate;

public class ArrayQueueADT {
    private Object[] elements = new Object[1];
    private int size = 0;
    private int front = 0;
    private int last = -1;

    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        if (queue.size == queue.elements.length)
            ensureCapacity(queue);
        queue.last = (queue.last + 1) % queue.elements.length;
        queue.elements[queue.last] = element;
        queue.size++;
    }

    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size != 0;
        Object res = queue.elements[queue.front];
        queue.elements[queue.front] = null;
        queue.front = (queue.front + 1) % queue.elements.length;
        queue.size--;
        return res;
    }

    public static Object element(ArrayQueueADT queue) {
        assert queue.size != 0;
        return queue.elements[queue.front];
    }
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    public static void clear(ArrayQueueADT queue) {
        queue.elements = new Object[1];
        queue.size = 0;
        queue.front = 0;
        queue.last = -1;
    }

    private static void ensureCapacity(ArrayQueueADT queue) {
        Object[] newQ = new Object[queue.elements.length * 2];
        int tale = queue.size - queue.elements.length + queue.front;
        if (tale < 0) {
            tale = 0;
        }

        System.arraycopy(queue.elements, queue.front, newQ, 0, queue.elements.length - queue.front);
        System.arraycopy(queue.elements, 0, newQ, queue.elements.length - queue.front, tale);

        queue.elements = newQ;
        queue.front = 0;
        queue.last = queue.size - 1;
    }

    public static int indexIf (ArrayQueueADT queue, Predicate<Object> pred) {
        int newEnd = queue.last;
        if (queue.last < queue.front) {
            newEnd = queue.size + queue.front - 1;
        }

        int start = queue.front;
        int end = newEnd;

        for (int i = start; i <= end; i += 1) {
            Object elem = queue.elements[i % queue.elements.length];
            if (elem != null && pred.test(elem)) {
                return Math.abs(i - queue.front);
            }
        }

        return -1;
    }

    public static int lastIndexIf(ArrayQueueADT queue, Predicate<Object> pred) {
        int newEnd = queue.last;
        if (queue.last < queue.front) {
            newEnd = queue.size + queue.front - 1;
        }

        int start = newEnd;
        int end = queue.front;

        for (int i = start; i >= end; i -= 1) {
            Object elem = queue.elements[i % queue.elements.length];
            if (elem != null && pred.test(elem)) {
                return Math.abs(i - queue.front);
            }
        }

        return -1;
    }
}
