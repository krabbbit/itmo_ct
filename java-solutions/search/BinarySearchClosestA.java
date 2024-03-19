package search;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchClosestA {
    public int l;
    public int r;

    //Pre: forall i in range(1, a.size()): a[i - 1] <= a[i] && x -> int, l < r -> int;
    // Post: R = alpha && alpha in a && forall i in range(0, a.size()): abs(a[i] - x) >= alpha
    public static int leftOrRight (List<Integer> a, int x, int l, int r) {
        //Pre: b = 0; Post: delta1 = b = 0
        int delta1 = 0;
        //Pre: b = 0; Post: delta2 = b = 0
        int delta2 = 0;
        // Pre: delta1 = delta2 = 0; Post: delta1 = Math.abs(a.get(l) - x)
        if (l >= 0) {
            delta1 = Math.abs(a.get(l) - x);
        }
        // Pre: delta2 = 0; Post: delta2 = Math.abs(a.get(r) - x);
        if (r < a.size()) {
            delta2 = Math.abs(a.get(r) - x);
        }
        // Pre: true; Post: Result = (a.get(l) || a.get(r)) || !(delta1 <= delta2 && l >= 0) && !(r < a.size())
        if (delta1 <= delta2 && l >= 0) {
            //Pre: delta1 <= delta2 && l >= 0; Post: R = a.get(l)
            return a.get(l);
        } else if (r < a.size()){
            //Pre: delta1 > delta2 || l < 0 && r < a.size(); Post: R = a.get(r)
            return a.get(r);
        }
        // Pre: !(delta1 <= delta2 && l >= 0) && !(r < a.size()); Post: R = (a.get(0) || a.get(a.size() - 1)) || a.size() == 0
        if (!a.isEmpty() && x < a.get(0)) {
            //Pre: E && !a.isEmpty() && x < a.get(0); Post: Result
            return a.get(0);
        } else if (!a.isEmpty()){
            //Pre: E && !a.isEmpty(); Post: Result
            return a.get(a.size() - 1);
        }
        //Pre: a.size() = 0; Post: Result = 0
        return 0;
    }

    // :NOTE: contract?
    public static void main(String[] args) {
        //Pre: b = 0; Post: x = b = 0
        int x = 0;
        // :NOTE: int[]
        List<Integer> a = new ArrayList<>();
        //Pre: args.size != 0 && x' == int(args[0]), Post: x = x'
        try {
            x = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid x");
        }
        //Pre: args.size > 1 Post: x = x' && forall i in range(1, args.size): a[i-1] = int(args[i])
        try {
            for (int i = 1; i < args.length; i++) {
                a.add(Integer.parseInt(args[i]));
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid elements in array");
        }
        // :NOTE: Choice?
        //Pre: b = new BinarySearch(); Post: obj = b = new BinarySearch();
        BinarySearch obj = new BinarySearch();
        //Pre: forall i in range(1, a.size()): a[i - 1] <= a[i] && x -> int, l < r -> int;
        // Post: a[obj.l] <= x && a[obj.r] >= x
        obj.binIter(a.size(), a, x, true);
        //Pre: forall i in range(1, a.size()): a[i - 1] <= a[i] && x -> int, l < r -> int;
        // Post: R = alpha && alpha in a && forall i in range(0, a.size()): abs(a[i] - x) >= alpha
        System.out.println(leftOrRight(a, x, obj.l, obj.r));
    }
}
