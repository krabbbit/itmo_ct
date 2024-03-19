package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BinarySearch {
    public int l;
    public int r;
    //Pre: forall i in range(1, a.size()): a[i - 1] <= a[i] ||
    // forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int;
    // Post: a[obj.l] <= x && a[obj.r] >= x
    // Inv: n >= 0
    public int binIter(int n, List<Integer> a, int x, boolean isUp) {
        int l = -1; // Pred: b = -1, Post: l = b = -1
        int r = n; // Pred: b = n, Post: r = b = n
        while (r - l > 1) { //Inv: a[0...l-1] <= x < a[r...n-1] && r - l > 1
            int m = (r + l) / 2; // Pre: Inv && (r+l)/2 = b < n; Post: Inv && m = b
            if (!isUp && a.get(m) > x || isUp && a.get(m) <= x) //Post: Inv && m = b &&
                // && (!isUp && a.get(m) > x || isUp && a.get(m) <= x) --> I
                l = m; // Pre: Inv && m = b && a[m] > x && m -> int && I; Post: Inv && m = b && a[m] > x && l = m
            else //Post: Inv && m = b && a[m] <= x
                r = m; // Pre: Inv && m = b && a[m] <= x && m -> int && !I; Post: Inv && m = b && a[m] <= x && r = m
        }
        // Pre: l -> int; Post: this.l = l
        this.l = l;
        // Pre: r -> int; Post: this.r = r
        this.r = r;
        if (l >= 0 && a.get(l) == x) // Pre: this.l = l && this.r = r; Post: R = i: i -> min a[i] <= x
            return l; // Pre: I && (r-l) <= 1 && l >= 0 && a[l] = x; Post: R = i: i -> min a[i] <= x
        else
            return r; // Pre: I && (r-l) <= 1 && !(l >= 0 && a[l] = x); Post: R = i: i -> min a[i] <= x
    }



    //Pre: forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int, l < r -> int
    // Post: a[obj.l] <= x && a[obj.r] >= x && R : R --> min a[R] <= x
    // Inv: n >= 0
    public static int recursion(int n, List<Integer> a, int x, int l, int r) {
        //Pre: forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int, l < r -> int
        // Post: (r - l) > 1 && ((l >= 0 && a[l] = x && l : l --> min a[l] <= x) ||
        // || ((l < 0 || a[l] != x) && r : r --> min a[r] <= x)) ---> E
        if ((r - l) <= 1) {
            // Pre: (r - l) <= 1 Post: E
            if (l >= 0 && a.get(l) == x)
                // Pre: (r - l) <= 1 && ((l >= 0 && a[l] = x && l -> int); Post: l : l --> min a[l] <= x
                return l;
            else
                // Pre: (r - l) <= 1 && ((l < 0 || a[l] != x) && r -> int; Post: r --> min a[r] <= x
                return r;
        }
        //Pred: E && (r + l) / 2 = b < n; Post: E && (m = b)
        int m = (r + l) / 2;
        // Pred: E && (m = b); Post: E ∧ (l || r) = m
        if (a.get(m) > x)
            // Pre: E && (m = b) && a[m] > x && m < n; Post: E && l = m
            l = m;
        else
            // Pre: E && (m = b) && a[m] <= x && m < n; Post: E && r = m
            r = m;
        // Pre: forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int, l < r -> int
        // Post: a[obj.l] <= x && a[obj.r] >= x && R : R --> min a[R] <= x
        return recursion(n, a, x, l, r);
    }

    // Pre: forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int, l < r -> int
    // Post: a[obj.l] <= x && a[obj.r] >= x && R : R --> min a[R] <= x
    public static int binRecur(int n, List<Integer> a, int x) {
        // Pre: forall i in range(1, a.size()): a[i - 1] >= a[i] && x -> int, l < r -> int
        // Post: a[obj.l] <= x && a[obj.r] >= x && R : R --> min a[R] <= x
        return recursion(n, a, x, -1, n);
    }


    public static void main(String[] args) {
        //Pre: b = 0; Post: x = b = 0
        int x = 0;
        //Pre: b = 0; Post: sum = b = 0
        int sum = 0;
        List<Integer> a = new ArrayList<>();
//        Scanner sc = new Scanner(System.in);
//        x = sc.nextInt();
//        int n = 1;
//        for(int i = 0; i < n; i++) {
//            a.add(sc.nextInt());
//        }
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
                sum += Integer.parseInt(args[i]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid elements in array");
        }
        //Pre: sum -> int; Post: a[obj.l] <= x && a[obj.r] >= x && R : R --> min a[R] <= x ---> I
        if (sum % 2 == 0) {
            //Pre: sum % 2 = 0; Post: I
            System.out.println(binRecur(a.size(), a, x));
        } else {
            //Pre: sum % 2 != 0; Post: I
            System.out.println((new BinarySearch()).binIter(a.size(), a, x, false));
        }
    }
}
