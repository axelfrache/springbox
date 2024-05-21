package io.github.axelfrache.savesync.controller;

import io.github.axelfrache.savesync.model.File;
import io.github.axelfrache.savesync.model.Folder;
import io.github.axelfrache.savesync.model.User;
import io.github.axelfrache.savesync.repository.FileRepository;
import io.github.axelfrache.savesync.repository.FolderRepository;
import io.github.axelfrache.savesync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/savesync/files")
    public String listFiles(@RequestParam(required = false) Long folderId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (!optionalUser.isPresent()) {
            return "error";
        }
        User user = optionalUser.get();
        Folder currentFolder = null;
        List<File> files;
        List<Folder> folders;

        if (folderId != null) {
            currentFolder = folderRepository.findById(folderId).orElse(null);
            files = fileRepository.findByFolderAndUser(currentFolder, user);
            folders = folderRepository.findByParentFolder(currentFolder);
        } else {
            files = fileRepository.findByUserAndFolderIsNull(user);
            folders = folderRepository.findByUserAndParentFolderIsNull(user);
        }

        model.addAttribute("files", files);
        model.addAttribute("folders", folders);
        model.addAttribute("currentFolder", currentFolder);
        return "files";
    }

    @PostMapping("/savesync/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long folderId, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (!optionalUser.isPresent()) {
            return "error";
        }
        User user = optionalUser.get();
        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findById(folderId).orElse(null);
        }
        String fileName = file.getOriginalFilename();
        String filePath = "uploads/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(file.getBytes());
        }
        File dbFile = new File();
        dbFile.setName(fileName);
        dbFile.setPath(filePath);
        dbFile.setUploadDate(new Date());
        dbFile.setUser(user);
        dbFile.setFolder(folder);
        fileRepository.save(dbFile);
        return "redirect:/savesync/files?folderId=" + (folder != null ? folder.getId() : "");
    }

    @PostMapping("/savesync/folder")
    public String createFolder(@RequestParam("name") String name, @RequestParam(required = false) Long parentFolderId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (!optionalUser.isPresent()) {
            return "error";
        }
        User user = optionalUser.get();
        Folder parentFolder = null;
        if (parentFolderId != null) {
            parentFolder = folderRepository.findById(parentFolderId).orElse(null);
        }
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUser(user);
        folder.setParentFolder(parentFolder);
        folderRepository.save(folder);
        return "redirect:/savesync/files?folderId=" + (parentFolder != null ? parentFolder.getId() : "");
    }

    @PostMapping("/savesync/folder/delete")
    public String deleteFolder(@RequestParam("id") Long id) {
        folderRepository.deleteById(id);
        return "redirect:/savesync/files";
    }

    @PostMapping("/savesync/file/delete")
    public String deleteFile(@RequestParam("id") Long id) {
        fileRepository.deleteById(id);
        return "redirect:/savesync/files";
    }
}
