import java.util.*;
import java.io.File; 

public class ReverseMinC {
    public static int[][] resultArray = new int[2][];
    public static int[] numbers = new int[2];


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

    public static void main(String[] args){
        int indexNumbers = 0;
        int indexResult = 0;
        int maxLenMod = 0;

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
            numbers = arrayCreate(numbers, indexNumbers);
            if(numbers.length > maxLenMod) {
              maxLenMod = numbers.length;
            }
            addElem(numbers, indexResult);
            indexResult += 1;
            scannerLine.close();
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
            if(resultArray[i] != null) {
                for(int j = 0; j < resultArray[i].length; j++) {
                   if(resultArray[i][j] < minArray[j]) {
                      minArray[j] = resultArray[i][j];
                   }
                   System.out.print(minArray[j]);
                   System.out.print(" ");
                }
                System.out.println();
            }
        }

        scannerSystem.close();
    }
}