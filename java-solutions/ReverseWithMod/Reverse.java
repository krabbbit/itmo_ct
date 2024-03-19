import java.util.*;
import java.io.File; 

public class Reverse {
    public static int[][] resultArray = new int[2][];
    public static int[] numbers = new int[2];
    // public static int indexNumbers = 0;
    // public static int indexResult = 0;


    public static int[] arrayCreate(int[] array, int len) {
        int[] newArray = new int[len];
        if(len == array.length*2) {
            System.arraycopy(array, 0, newArray, 0, array.length);
        }
        else {
            System.arraycopy(array, 0, newArray, 0, len);
        }
        return newArray;
    }

    public static int[][] arrayCreate(int[][] array, int len) {
        int[][] newArray = new int[len][];
        if(len == array.length*2) {
            System.arraycopy(array, 0, newArray, 0, array.length);
        }
        else {
            System.arraycopy(array, 0, newArray, 0, len);
        }
        return newArray;
    }

    public static void addElem(int elem, int index) {
        if(index >= numbers.length) {
            numbers = arrayCreate(numbers, numbers.length*2);
        }
        numbers[index] = elem;
    }

    public static void addElem(int[] elem, int index) {
        if(index >= resultArray.length) {
            resultArray = arrayCreate(resultArray, resultArray.length*2);
        }
        resultArray[index] = elem;
    }

    public static void main(String[] args) throws Exception {
        //ArrayList<String[]> resultArray = new ArrayList<String[]>();
        //String[] numbers;
        // int[][] resultArray = new int[2][];
        // int[] numbers = new int[2];
        int indexNumbers = 0;
        int indexResult = 0;

        Scanner scannerSystem = new Scanner(System.in); //new File("C:\\Java\\HW3.Reverse\\test.txt")
        while(scannerSystem.hasNextLine()) {
            indexNumbers = 0;
            numbers = new int[1];
            Scanner scannerLine = new Scanner(scannerSystem.nextLine());
            while(scannerLine.hasNextInt()) {
               // int next = Integer.parseInt(scannerLine.next(), 16);
                addElem(scannerLine.nextInt(), indexNumbers);
                indexNumbers += 1;
            }
            // System.out.println("!!!");
            // for(int k:numbers){
            //   if(k != 0){
            //     System.out.print(k + " ");
            //   }
            //   System.out.println();
            // }
            numbers = arrayCreate(numbers, indexNumbers);
            addElem(numbers, indexResult);
            indexResult += 1;
            scannerLine.close();
        }
        resultArray = arrayCreate(resultArray, indexResult);
        // for(int[] k: resultArray) {
        //     if(k != null) {
        //         for(int p: k) {
        //             if(p != 0){
        //                 System.err.print(p);
        //                 System.err.print(" ");
        //             }
                    
        //         }
        //         System.err.println();   
        //     }         
        // }

        for(int i = resultArray.length - 1; i >= 0; i--) {
            if(resultArray[i] != null) {
                for(int j = resultArray[i].length - 1; j >= 0; j--) {
                   // if(resultArray[i][j] != 0) {
                        System.out.print(resultArray[i][j]);
                        System.out.print(" ");
                   // }
                }
                System.out.println();
            }
            // else {
            //   System.out.println();
            // }
            
        }

        scannerSystem.close();
    }
}