package com.tj.petstore.util;

import java.util.Random;

public class Utility {

    public static int generateId() {
        Random rand = new Random();
        return rand.nextInt(1_000_000); // Generates ID between 0 and 999999
    }
}
