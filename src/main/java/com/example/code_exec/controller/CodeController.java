package com.example.code_exec.controller;
import com.example.code_exec.model.CodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class CodeController {
    @Autowired
    @PostMapping("/execute")
    public String execute(@RequestBody CodeRequest request) {

        String code = request.getCode();
        String input = request.getInput();
        String language = request.getLanguage();

        try {
            String folderName = "temp_" + UUID.randomUUID();
            File dir = new File(folderName);
            dir.mkdir();

            String fileName = "";
            Process compile = null;
            Process run = null;

            switch (language) {

                case "java":
                    fileName = "Main.java";
                    writeFile(dir, fileName, code);

                    compile = new ProcessBuilder("javac", fileName)
                            .directory(dir)
                            .start();

                    compile.waitFor();

                    String compileError = readStream(compile.getErrorStream());
                    if (!compileError.isEmpty()) return compileError;

                    run = new ProcessBuilder("java", "Main")
                            .directory(dir)
                            .start();
                    break;

                case "python":
                    fileName = "main.py";
                    writeFile(dir, fileName, code);

                    run = new ProcessBuilder("python", fileName)
                            .directory(dir)
                            .start();
                    break;

                case "cpp":
                    fileName = "main.cpp";
                    writeFile(dir, fileName, code);

                    compile = new ProcessBuilder("g++", fileName, "-o", "a.out")
                            .directory(dir)
                            .start();

                    compile.waitFor();

                    compileError = readStream(compile.getErrorStream());
                    if (!compileError.isEmpty()) return compileError;

                    run = new ProcessBuilder("./a.out")
                            .directory(dir)
                            .start();
                    break;

                case "c":
                    fileName = "main.c";
                    writeFile(dir, fileName, code);

                    compile = new ProcessBuilder("gcc", fileName, "-o", "a.out")
                            .directory(dir)
                            .start();

                    compile.waitFor();

                    compileError = readStream(compile.getErrorStream());
                    if (!compileError.isEmpty()) return compileError;

                    run = new ProcessBuilder("./a.out")
                            .directory(dir)
                            .start();
                    break;

                case "js":
                    fileName = "main.js";
                    writeFile(dir, fileName, code);

                    run = new ProcessBuilder("node", fileName)
                            .directory(dir)
                            .start();
                    break;

                default:
                    return "Unsupported language";
            }

            if (input != null && !input.isEmpty()) {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(run.getOutputStream())
                );
                writer.write(input);
                writer.newLine();
                writer.flush();
                writer.close();
            }

            run.waitFor(5, TimeUnit.SECONDS);

            String output = readStream(run.getInputStream());
            String error = readStream(run.getErrorStream());

            return output + error;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void writeFile(File dir, String name, String code) throws IOException {
        File file = new File(dir, name);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }
}
