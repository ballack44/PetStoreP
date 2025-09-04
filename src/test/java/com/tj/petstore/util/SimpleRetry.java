package com.tj.petstore.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class SimpleRetry implements IRetryAnalyzer {
    private int count = 0;
    public boolean retry(ITestResult result) {
        return count++ < 5; //
    }
}
