package com.example.code_exec.service;

import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class CodeService {

    public String executeCode(String code) {
        try {
            File file = new File("Main.java");
            FileWriter writer = new FileWriter(file);
            writer.write(code);
            writer.close();

            Process compile = Runtime.getRuntime().exec("javac Main.java");
            compile.waitFor();

            Process run = Runtime.getRuntime().exec("java Main");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(run.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
