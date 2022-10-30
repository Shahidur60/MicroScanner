package com.msscanner.msscanner.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class FileUtil {

    public String getMicroservicePath() throws FileNotFoundException {
        File file = new File("microservice-compile-path.txt");
        Scanner sc = new Scanner(file);
        String filePath = "";
        while (sc.hasNextLine()) {
            filePath = sc.next();
        }

        return filePath;
    }
}