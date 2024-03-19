package queue;

import java.util.function.Predicate;

public class ArrayQueueModule {
    private static Object[] elements = new Object[1];
    private static int size = 0;
    private static int front = 0;
    private static int last = -1;

    public static void enqueue(Object element) {
        assert element != null;
        if (size == elements.length)
            ensureCapacity();
        last = (last + 1) % elements.length;
        elements[last] = element;
        size++;
    }

    public static Object dequeue() {
        assert size != 0;
        Object res = elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        return res;
    }

    public static Object element() {
        assert size != 0;
        return elements[front];
    }

    public static int size() {
        return size;
    }

    public static boolean isEmpty() {
        return size == 0;
    }

    public static void clear() {
        elements = new Object[1];
        size = 0;
        front = 0;
        last = -1;
    }

    private static void ensureCapacity() {
        Object[] newQ = new Object[elements.length * 2];
        int tale = size - elements.length + front;
        if (tale < 0) {
            tale = 0;
        }

        System.arraycopy(elements, front, newQ, 0, elements.length - front);
        System.arraycopy(elements, 0, newQ, elements.length - front, tale);

        elements = newQ;
        front = 0;
        last = size - 1;
    }

    public static int indexIf (Predicate<Object> pred) {
        int newEnd = last;
        if (last < front) {
            newEnd = size + front - 1;
        }

        int start = front;
        int end = newEnd;

        for (int i = start; i <= end; i += 1) {
            Object elem = elements[i % elements.length];
            if (elem != null && pred.test(elem)) {
                return Math.abs(i - front);
            }
        }

        return -1;
    }
    public static int lastIndexIf(Predicate<Object> pred) {
        int newEnd = last;
        if (last < front) {
            newEnd = size + front - 1;
        }

        int start = newEnd;
        int end = front;

        for (int i = start; i >= end; i -= 1) {
            Object elem = elements[i % elements.length];
            if (elem != null && pred.test(elem)) {
                return Math.abs(i - front);
            }
        }

        return -1;
    }
}
