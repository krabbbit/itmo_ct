package queue;

import java.util.HashSet;
import java.util.Set;

public class LinkedQueue extends AbstractQueue {
    private Node tail;
    private Node head;

    public LinkedQueue() {
        tail = null;
        head = null;
        size = 0;
    }

    @Override
    protected void enqueueImpl(Object element) {
        Node back = tail;
        tail = new Node(element, null);
        if (size == 0) {
            head = tail;
        } else {
            back.next = tail;
        }
    }

    protected void dequeueImpl() {
        head = head.next;
    }

    protected Object elementImpl() {
        return head.value;
    }

    protected void clearImpl() {
        head = null;
        tail = null;
    }


    private class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            assert value != null;

            this.value = value;
            this.next = next;
        }
    }
}
