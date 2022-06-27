package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

import static org.example.PrimeValid.isPrime;

public class PrimeService {
    private final List<Integer> arr;

    public PrimeService(List<Integer> arr) {
        this.arr = arr;
        fromInputToArray();
    }

    private void fromInputToArray(){
        try {
            File myObj = new File("test.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                arr.add(Integer.parseInt(myReader.nextLine()));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public BigInteger makePrime(int size){
        int finalSize = size/8;
        String stringVal = "";
        String bin = "";
        int counter = 0;
        for (Integer integer : arr) {
            if (counter == finalSize) {
                BigInteger number = new BigInteger(stringVal, 2);
                if (isPrime(number)) {
                    return number;
                } else {
                    stringVal = "";
                    counter = 0;
                }
            } else if (integer >= 128 && integer <= 255) {
                bin = Integer.toBinaryString(integer);
                stringVal = stringVal.concat(bin);
                counter++;
            }
        }
        throw new RuntimeException("Prime number does not exist");
    }

    public BigInteger makeDifferentPrime(BigInteger givenPrime){
        int finalSize = givenPrime.bitLength()/8;
        String stringVal = "";
        String bin = "";
        int counter = 0;
        for (Integer integer : arr) {
            if (counter == finalSize) {
                BigInteger number = new BigInteger(stringVal, 2);
                if (isPrime(number) && number.compareTo(givenPrime) != 0) {
                    return number;
                } else {
                    stringVal = "";
                    counter = 0;
                }
            } else if (integer >= 128 && integer <= 255) {
                bin = Integer.toBinaryString(integer);
                stringVal = stringVal.concat(bin);
                counter++;
            }
        }
        throw new RuntimeException("Prime number does not exist");
    }
}
