package io.github.axelfrache.savesync.controller;

import io.github.axelfrache.savesync.model.File;
import io.github.axelfrache.savesync.model.User;
import io.github.axelfrache.savesync.repository.FileRepository;
import io.github.axelfrache.savesync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/savesync")
public class FileController {

    private static final Logger logger = Logger.getLogger(FileController.class.getName());

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/files")
    public String listFiles(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            List<File> files = fileRepository.findByUser(user);
            model.addAttribute("files", files);
        } else {
            model.addAttribute("files", List.of());
        }
        return "files";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            String fileName = file.getOriginalFilename();
            String filePath = "uploads/" + fileName;
            logger.info("Uploading file: " + fileName + " to path: " + filePath);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                logger.severe("Error writing file: " + e.getMessage());
                throw e;
            }
            File dbFile = new File();
            dbFile.setName(fileName);
            dbFile.setPath(filePath);
            dbFile.setUploadDate(new Date());
            dbFile.setUser(user);
            fileRepository.save(dbFile);
            logger.info("File uploaded and saved to database: " + fileName);
        }
        return "redirect:/savesync/files";
    }

    @GetMapping("/files/download/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException {
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found with id " + id));
        Path path = Paths.get(file.getPath());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(path))
                .body(resource);
    }
}