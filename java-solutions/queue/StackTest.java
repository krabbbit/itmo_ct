//package queue;
//
//public class StackTest {
//    public static void fill(Queue stack) {
//        for (int i = 0; i < 3; i++) {
//            stack.enqueue(i);
//        }
//    }
//
//    public static void dump(Queue stack) {
//        while (!stack.isEmpty()) {
//            System.out.println(stack.size() + " " +
//                stack.element() + " " + stack.dequeue());
//        }
//    }
//
//    public static void test(Queue stack) {
//        fill(stack);
//
//        dump(stack);
//        System.out.println();
//
//    }
//
//    public static void main(String[] args) {
//        test(new ArrayQueue());
//        test(new LinkedQueue());
//        AbstractQueue queue = new LinkedQueue();
//    }
//}
