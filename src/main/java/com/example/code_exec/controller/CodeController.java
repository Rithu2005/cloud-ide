//package com.example.code_exec.controller;
//
//import com.example.code_exec.model.CodeRequest;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.*;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//@CrossOrigin
//public class CodeController {
//
//    @PostMapping("/execute")
//    public String executeCode(@RequestBody CodeRequest request) {
//        String code = request.getCode();
//        String input = request.getInput();
//
//        try {
//            // 🧱 Step 1: Create temp directory
//            File dir = new File("temp");
//            if (!dir.exists()) dir.mkdir();
//
//            // 🧾 Step 2: Write code to Main.java
//            File file = new File(dir, "Main.java");
//            try (FileWriter writer = new FileWriter(file)) {
//                writer.write(code);
//            }
//
//            // ⚙️ Step 3: Compile
//            Process compile = new ProcessBuilder("javac", "Main.java")
//                    .directory(dir)
//                    .start();
//
//            compile.waitFor();
//
//            BufferedReader compileError = new BufferedReader(
//                    new InputStreamReader(compile.getErrorStream())
//            );
//
//            String line;
//            StringBuilder error = new StringBuilder();
//
//            while ((line = compileError.readLine()) != null) {
//                error.append(line).append("\n");
//            }
//
//            if (error.length() > 0) {
//                return "Compilation Error:\n" + error;
//            }
//
//            // ▶️ Step 4: Run
//            Process run = new ProcessBuilder("java", "Main")
//                    .directory(dir)
//                    .start();
//
//            // ✨ Step 5: Send input
//            if (input != null && !input.isEmpty()) {
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(run.getOutputStream())
//                );
//                writer.write(input);
//                writer.newLine();
//                writer.flush();
//                writer.close();
//            }
//
//            // ⏱ Step 6: Timeout protection
//            run.waitFor(5, TimeUnit.SECONDS);
//
//            // 📤 Step 7: Read output
//            BufferedReader outputReader = new BufferedReader(
//                    new InputStreamReader(run.getInputStream())
//            );
//
//            StringBuilder output = new StringBuilder();
//            while ((line = outputReader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            // ❗ Step 8: Read runtime errors
//            BufferedReader errorReader = new BufferedReader(
//                    new InputStreamReader(run.getErrorStream())
//            );
//
//            while ((line = errorReader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            return output.toString();
//
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//}
//package com.example.code_exec.controller;
//
//import com.example.code_exec.model.CodeRequest;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//@CrossOrigin
//public class CodeController {
//
//    private static final String BASE_DIR = "sessions";
//
//    // ✅ EXECUTE CODE
//    @PostMapping("/execute")
//    public String executeCode(@RequestBody CodeRequest request) {
//
//        String code = request.getCode();
//        String input = request.getInput();
//        String sessionId = request.getSessionId();
//
//        if (sessionId == null || sessionId.isEmpty()) {
//            sessionId = UUID.randomUUID().toString();
//        }
//
//        File dir = new File(BASE_DIR + "/" + sessionId);
//        if (!dir.exists()) dir.mkdirs();
//
//        try {
//            // Write code
//            File file = new File(dir, "Main.java");
//            try (FileWriter writer = new FileWriter(file)) {
//                writer.write(code);
//            }
//
//            // Compile
//            Process compile = new ProcessBuilder("javac", "Main.java")
//                    .directory(dir)
//                    .start();
//
//            compile.waitFor();
//
//            String compileError = readStream(compile.getErrorStream());
//            if (!compileError.isEmpty()) {
//                return "Compilation Error:\n" + compileError;
//            }
//
//            // Run
//            Process run = new ProcessBuilder("java", "Main")
//                    .directory(dir)
//                    .start();
//
//            // Input
//            if (input != null && !input.isEmpty()) {
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(run.getOutputStream())
//                );
//                writer.write(input);
//                writer.newLine();
//                writer.flush();
//                writer.close();
//            }
//
//            run.waitFor(5, TimeUnit.SECONDS);
//
//            String output = readStream(run.getInputStream());
//            String error = readStream(run.getErrorStream());
//
//            return output + error;
//
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//
//    // ✅ FILE EXPLORER
//    @GetMapping("/files/{sessionId}")
//    public String[] listFiles(@PathVariable String sessionId) {
//        File dir = new File(BASE_DIR + "/" + sessionId);
//        if (!dir.exists()) return new String[]{};
//
//        return dir.list();
//    }
//
//    // ✅ SAVE FILE
//    @PostMapping("/save")
//    public String saveFile(@RequestBody CodeRequest request) throws IOException {
//
//        String sessionId = request.getSessionId();
//        String fileName = request.getFileName();
//        String code = request.getCode();
//
//        File dir = new File(BASE_DIR + "/" + sessionId);
//        if (!dir.exists()) dir.mkdirs();
//
//        File file = new File(dir, fileName);
//        try (FileWriter writer = new FileWriter(file)) {
//            writer.write(code);
//        }
//
//        return "Saved";
//    }
//
//    // ✅ DOWNLOAD FILE
//    @GetMapping("/download/{sessionId}/{fileName}")
//    public byte[] downloadFile(@PathVariable String sessionId,
//                               @PathVariable String fileName) throws IOException {
//
//        Path path = Paths.get(BASE_DIR + "/" + sessionId + "/" + fileName);
//        return Files.readAllBytes(path);
//    }
//
//    // ✅ SHARE LINK (returns session ID)
//    @PostMapping("/share")
//    public String share(@RequestBody CodeRequest request) throws IOException {
//
//        String sessionId = UUID.randomUUID().toString();
//
//        File dir = new File(BASE_DIR + "/" + sessionId);
//        dir.mkdirs();
//
//        File file = new File(dir, "Main.java");
//        try (FileWriter writer = new FileWriter(file)) {
//            writer.write(request.getCode());
//        }
//
//        return sessionId;
//    }
//
//    // ✅ LOAD SHARED CODE
//    @GetMapping("/load/{sessionId}")
//    public String load(@PathVariable String sessionId) throws IOException {
//
//        Path path = Paths.get(BASE_DIR + "/" + sessionId + "/Main.java");
//
//        if (!Files.exists(path)) return "Not found";
//
//        return new String(Files.readAllBytes(path));
//    }
//
//    // 🔧 Helper
//    private String readStream(InputStream stream) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//        StringBuilder output = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            output.append(line).append("\n");
//        }
//        return output.toString();
//    }
//}



package com.example.code_exec.controller;

import com.example.code_exec.model.CodeRequest;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class CodeController {

    @PostMapping("/execute")
    public String execute(@RequestBody CodeRequest request) {

        String code = request.getCode();
        String input = request.getInput();
        String language = request.getLanguage();

        try {
            // 🔥 Create unique folder per request
            String folderName = "temp_" + UUID.randomUUID();
            File dir = new File(folderName);
            dir.mkdir();

            String fileName = "";
            Process compile = null;
            Process run = null;

            /* =========================
               FILE CREATION
            ========================= */
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

            /* =========================
               INPUT
            ========================= */
            if (input != null && !input.isEmpty()) {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(run.getOutputStream())
                );
                writer.write(input);
                writer.newLine();
                writer.flush();
                writer.close();
            }

            /* =========================
               TIMEOUT
            ========================= */
            run.waitFor(5, TimeUnit.SECONDS);

            /* =========================
               OUTPUT
            ========================= */
            String output = readStream(run.getInputStream());
            String error = readStream(run.getErrorStream());

            return output + error;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /* =========================
       WRITE FILE
    ========================= */
    private void writeFile(File dir, String name, String code) throws IOException {
        File file = new File(dir, name);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
    }

    /* =========================
       READ STREAM
    ========================= */
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
