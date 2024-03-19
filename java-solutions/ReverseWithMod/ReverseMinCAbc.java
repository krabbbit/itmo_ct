import java.io.*;
import java.util.*;


public class ReverseMinCAbc {
    public static int[][] resultArray = new int[2][];
    public static int[] numbers = new int[2];


    public static int[] arrayCreate (int[] array, int len) {
        int[] newArray = new int[len];
        if (len == array.length*2) {
            System.arraycopy(array, 0, newArray, 0, array.length);
        }
        else {
            System.arraycopy(array, 0, newArray, 0, len);
        }
        return newArray;
    }

    public static int[][] arrayCreate (int[][] array, int len) {
        int[][] newArray = new int[len][];
        if (len == array.length*2) {
            System.arraycopy(array, 0, newArray, 0, array.length);
        }
        else {
            System.arraycopy(array, 0, newArray, 0, len);
        }
        return newArray;
    }

    public static void addElem (int elem, int index) {
        if (index >= numbers.length) {
            numbers = arrayCreate(numbers, numbers.length*2);
        }
        numbers[index] = elem;
    }

    public static void addElem (int[] elem, int index) {
        if (index >= resultArray.length) {
            resultArray = arrayCreate(resultArray, resultArray.length*2);
        }
        resultArray[index] = elem;
    }

    public static void main (String[] args) {
        int indexNumbers = 0;
        int indexResult = 0;
        int maxLenMod = 0;
        long END = Integer.MAX_VALUE + 1;
        long NOLL = Integer.MIN_VALUE - 1;

        MyScanner scannerSystem = new MyScanner(new InputStreamReader(System.in));
        String nextLinee = scannerSystem.nextLine();
        MyScanner scannerLine;
        while (!Objects.equals(nextLinee, "/end")) {
            if (!Objects.equals(nextLinee, "/null")) {
                indexNumbers = 0;
                numbers = new int[1];
                scannerLine = new MyScanner(nextLinee);
                String nextNum = scannerLine.nextWord("word");
                while (!Objects.equals(nextNum, "/end")) {
                    if (!Objects.equals(nextNum, "/null")) {
                        addElem(MyScanner.outABC(nextNum), indexNumbers);
                        indexNumbers += 1;
                    }
                    nextNum = scannerLine.nextWord("word");
                }
                numbers = arrayCreate(numbers, indexNumbers);
                if(numbers.length > maxLenMod) {
                    maxLenMod = numbers.length;
                }
                addElem(numbers, indexResult);
                indexResult += 1;
                scannerLine.close();
            }
            
            nextLinee = scannerSystem.nextLine();
        }
        resultArray = arrayCreate(resultArray, indexResult);

        int[] minArray = new int[maxLenMod];
        int maxInt = Integer.MAX_VALUE;
        for(int i = 0; i < maxLenMod; i++) {
            if(i < resultArray[0].length) {
                minArray[i] = resultArray[0][i];
            }
            else {
                minArray[i] = maxInt;
            }
        }

        for(int i = 0; i < resultArray.length; i++) {
            for(int j = 0; j < resultArray[i].length; j++) {
               if(resultArray[i][j] < minArray[j]) {
                  minArray[j] = resultArray[i][j];
               }
               System.out.print(MyScanner.inABC(minArray[j]));
               System.out.print(" ");
            }
            System.out.println();
        }

        scannerSystem.close();
    }
}

