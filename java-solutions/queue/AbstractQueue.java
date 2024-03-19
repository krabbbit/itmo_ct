package queue;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    @Override
    public void enqueue(Object element) {
        assert element != null;

        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object element() {
        assert size > 0;

        return elementImpl();
    }

    protected abstract Object elementImpl();

    @Override
    public Object dequeue() {
        assert size > 0;

        Object result = element();
        size--;
        dequeueImpl();
        return result;
    }

    protected abstract void dequeueImpl();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        clearImpl();
        size = 0;
    }

    protected abstract void clearImpl();

    //
    @Override
    public void distinct() {
        Set<Object> set = new HashSet<>();
        int new_size = size;
        int i = 0;
        while(i < new_size){
            Object value = dequeue();
//            System.out.println(value);

            if (!set.contains(value)) {
                enqueue(value);
                set.add(value);
            }
            i++;
        }
    }
}
