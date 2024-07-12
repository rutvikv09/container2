package com.example.container2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@RestController
@RequestMapping("/calculate")
public class container2scan {

    @Value("${FILE_PATH}")
    private String filePath;

    @GetMapping
    public ResponseEntity<Map<String, Object>> operation(
            @RequestParam(value = "file") String file,
            @RequestParam(value = "product") String product) {

        Map<String, Object> output = new HashMap<>();
        calculateFile(output, file, product);
        return ResponseEntity.ok(output);
    }

    private void calculateFile(Map<String, Object> output, String file, String product) {
        String filePathWithName = String.format("%s/%s", filePath, file);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePathWithName))) {
            String line;
            int sum = 0;
            boolean isFirstLine = true; // Flag to skip the header line

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }

                String[] currLine = line.split(",");
                if (currLine.length != 2) {
                    output.put("file", file);
                    output.put("error", "Input file not in CSV format.");
                    return;
                }

                // Trim elements to remove any leading/trailing whitespace
                String productInFile = currLine[0].trim();
                String amountInFile = currLine[1].trim();

                if (productInFile.equals(product)) {
                    try {
                        sum += Integer.parseInt(amountInFile);
                    } catch (NumberFormatException e) {
                        output.put("file", file);
                        output.put("error", "Invalid number format in file.");
                        return;
                    }
                }
            }

            output.put("file", file);
            output.put("sum", sum);
        } catch (FileNotFoundException e) {
            output.put("file", file);
            output.put("error", "File not found.");
        } catch (IOException e) {
            output.put("file", file);
            output.put("error", "Error reading the input file.");
        }
    }

}
