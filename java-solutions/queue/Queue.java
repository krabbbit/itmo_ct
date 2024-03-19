package queue;

// Model: a[1]..a[n]
// Let: immutable(k): forall i=1..k: a'[i] = a[i]
// Inv: n >= 0 && forall i=1..n: a[i] != null
public interface Queue{
    // Pre: element != null
    // Post: n' = n + 1 && a[n'] = element && immutable(n)
    void enqueue(Object element);

    // Pre: n > 0
    // Post: n' = n - 1 &&
    //      R = a[n] && n' = n - 1 && immutable(n')
    Object dequeue();

    // Pre: n > 0
    // Post: R = a[n] && n' = n && immutable(n)
    Object element();

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    int size();

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    boolean isEmpty();

    // Pre: true
    // Post: a = [null] && size = 0 && n' = n && immutable(n)
    void clear();

    // Pre: true
    // Post: elements' = set(elements) && immutable(n')
    void distinct();
}
