//package queue;
//
//import java.util.function.Predicate;
//
//// Model: a[0]..a[n]
//// Let: immutable(k): forall i=0..k: a'[i] = a[i]
//// Inv: n >= 0 && forall i=1..n: a[i] != null
//public class ArrayQueue {
//    private Object[] elements = new Object[1];
//    private int size = 0;
//    private int front = 0;
//    private int last = -1;
//
//    // Pre: true, Post: R = new ArrayQueue();
//    public ArrayQueue() {
//    }
//
//
//    // Pre: element != null
//    // Post: n' = n + 1 && a'[n'] == element &&
//    //       immutable(n')
//    public void enqueue(Object element) {
//        assert element != null;
//        if (size == elements.length)
//            ensureCapacity();
//        last = (last + 1) % elements.length;
//        elements[last] = element;
//        size++;
//    }
//
//    // Pre: n > 0
//    // Post: n' = n - 1 &&
//    //      R = a[0] && a[0] == a[1], a[1] = a[2] ... a[n'] = a[n] && immutable(n')
//    public Object dequeue() {
//        assert size != 0;
//        Object res = elements[front];
//        elements[front] = null;
//        front = (front + 1) % elements.length;
//        size--;
//        return res;
//    }
//
//    // Pre: n > 0
//    // Post: R = a[0] && n' = n && immutable(n)
//    public Object element() {
//        assert size != 0;
//        return elements[front];
//    }
//
//    // Pre: true
//    // Post: R = n && n' = n && immutable(n)
//    public int size() {
//        return size;
//    }
//
//    // Pre: true
//    // Post: R = (n == 0) && n' = n && immutable(n)
//    public boolean isEmpty() {
//        return size == 0;
//    }
//
//    // Pre: true
//    // Post: a = [null] && n' = 0
//    public void clear() {
//        elements = new Object[1];
//        size = 0;
//        front = 0;
//        last = -1;
//    }
//
//    // Pre: true
//    // Post: n' = n * 2 && a[0..n] != null && a[n+1..n'] == null
//    private void ensureCapacity() {
//        Object[] newQ = new Object[elements.length * 2];
//        int tale = size - elements.length + front;
//        if (tale < 0) {
//            tale = 0;
//        }
//
//        System.arraycopy(elements, front, newQ, 0, elements.length - front);
//        System.arraycopy(elements, 0, newQ, elements.length - front, tale);
//
//        elements = newQ;
//        front = 0;
//        last = size - 1;
//    }
//
//    // Pre: true
//    // Post: min(x1, x2...x_k): pred(x_i) == true && immutable(n)
//    public int indexIf (Predicate<Object> pred) {
//        int newEnd = last;
//        if (last < front) {
//            newEnd = size + front - 1;
//        }
//
//        int start = front;
//        int end = newEnd;
//
//        for (int i = start; i <= end; i += 1) {
//            Object elem = elements[i % elements.length];
//            if (elem != null && pred.test(elem)) {
//                return Math.abs(i - front);
//            }
//        }
//
//        return -1;
//    }
//
//    // Pre: true
//    // Post: max(x1, x2...x_k): pred(x_i) == true && immutable(n)
//    public int lastIndexIf(Predicate<Object> pred) {
//        int newEnd = last;
//        if (last < front) {
//            newEnd = size + front - 1;
//        }
//
//        int start = newEnd;
//        int end = front;
//
//        for (int i = start; i >= end; i -= 1) {
//            Object elem = elements[i % elements.length];
//            if (elem != null && pred.test(elem)) {
//                return Math.abs(i - front);
//            }
//        }
//
//        return -1;
//    }
//}

package queue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[1];
    private int front = 0;
    private int last = -1;
    public ArrayQueue() {
    }


    public void enqueueImpl(Object element) {
        if (size == elements.length)
            ensureCapacity();
        last = (last + 1) % elements.length;
        elements[last] = element;
    }

    public void dequeueImpl() {
        elements[front] = null;
        front = (front + 1) % elements.length;
    }

    public Object elementImpl() {
        return elements[front];
    }


    protected void clearImpl() {
        elements = new Object[1];
        front = 0;
        last = -1;
    }

    private void ensureCapacity() {
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
    // Pre: true
    // Post: min(x1, x2...x_k): pred(x_i) == true && immutable(n)
    public int indexIf (Predicate<Object> pred) {
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

    // Pre: true
    // Post: max(x1, x2...x_k): pred(x_i) == true && immutable(n)
    public int lastIndexIf(Predicate<Object> pred) {
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

