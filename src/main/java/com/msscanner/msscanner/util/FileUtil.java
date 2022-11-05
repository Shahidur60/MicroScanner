package com.msscanner.msscanner.util;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class FileUtil {

    public String getMicroservicePath() throws FileNotFoundException {
        File file = new File("microservice-compile-path.txt");
        String filePath = "";
        Scanner sc = new Scanner(file);
        try {

            while (sc.hasNextLine()) {
                filePath = sc.next();
            }
        }finally {
            IOUtils.closeQuietly(sc);
        }


        return filePath;
    }
}