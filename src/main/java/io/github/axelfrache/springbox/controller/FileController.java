package io.github.axelfrache.springbox.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.github.axelfrache.springbox.model.File;
import io.github.axelfrache.springbox.model.Folder;
import io.github.axelfrache.springbox.model.User;
import io.github.axelfrache.springbox.repository.FileRepository;
import io.github.axelfrache.springbox.repository.FolderRepository;
import io.github.axelfrache.springbox.repository.UserRepository;
import jakarta.annotation.PostConstruct;

@Controller
public class FileController {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FolderRepository folderRepository;

	@Autowired
	private UserRepository userRepository;

	private final Path uploadDirectory = Paths.get("uploads");

	@PostConstruct
	public void init() {
		try {
			if (Files.notExists(uploadDirectory)) {
				Files.createDirectories(uploadDirectory);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize upload directory", e);
		}
	}

	@GetMapping("/springbox/files")
	public String listFiles(@RequestParam(required = false) Long folderId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}

		var user = optionalUser.get();
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

		for (var file : files) {
			try {
				var filePath = Paths.get(file.getPath());
				Files.readString(filePath);
				file.setEditable(true);
			} catch (IOException e) {
				file.setEditable(false);
			}
		}

		var totalSize = calculateTotalSize(user);

		Map<String, Double> mediaTypePercentages = calculateMediaTypePercentages(files);

		model.addAttribute("files", files);
		model.addAttribute("folders", folders);
		model.addAttribute("currentFolder", currentFolder);
		model.addAttribute("username", user.getUsername());
		model.addAttribute("totalSize", totalSize);
		model.addAttribute("mediaTypePercentages", mediaTypePercentages);
		return "files";
	}

	private Map<String, Double> calculateMediaTypePercentages(List<File> files) {
		Map<String, Long> mediaTypeCounts = new HashMap<>();
		var totalFiles = files.size();

		for (var file : files) {
			String mediaType = determineMediaType(file.getName()).toLowerCase();
			mediaTypeCounts.put(mediaType, mediaTypeCounts.getOrDefault(mediaType, 0L) + 1);
		}

		Map<String, Double> mediaTypePercentages = new HashMap<>();
		for (var entry : mediaTypeCounts.entrySet()) {
			mediaTypePercentages.put(entry.getKey(), (entry.getValue() * 100.0) / totalFiles);
		}

		return mediaTypePercentages;
	}

	private String determineMediaType(String fileName) {
		var extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
		return switch (extension) {
			case "jpg", "jpeg", "png", "gif" -> "Images";
			case "mp4", "avi", "mov" -> "Videos";
			case "mp3", "wav" -> "Audio";
			case "pdf", "doc", "docx" -> "Documents";
			default -> "Others";
		};
	}

	@PostMapping("/springbox/upload")
	public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam(required = false) Long folderId,
			@AuthenticationPrincipal UserDetails userDetails) throws IOException {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}
		var user = optionalUser.get();
		Folder folder = null;
		if (folderId != null) {
			folder = folderRepository.findById(folderId).orElse(null);
		}
		for (var file : files) {
			var fileName = file.getOriginalFilename();
			var filePath = "uploads/" + fileName;
			try (FileOutputStream fos = new FileOutputStream(filePath)) {
				fos.write(file.getBytes());
			}
			var dbFile = new File();
			dbFile.setName(fileName);
			dbFile.setPath(filePath);
			dbFile.setUploadDate(new Date());
			dbFile.setUser(user);
			dbFile.setFolder(folder);
			fileRepository.save(dbFile);
		}
		return "redirect:/springbox/files?folderId=" + (folder != null ? folder.getId() : "");
	}

	@PostMapping("/springbox/folder")
	public String createFolder(@RequestParam("name") String name, @RequestParam(required = false) Long parentFolderId,
			@AuthenticationPrincipal UserDetails userDetails) {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}
		var user = optionalUser.get();
		Folder parentFolder = null;
		if (parentFolderId != null) {
			parentFolder = folderRepository.findById(parentFolderId).orElse(null);
		}
		Folder folder = new Folder();
		folder.setName(name);
		folder.setUser(user);
		folder.setParentFolder(parentFolder);
		folderRepository.save(folder);
		return "redirect:/springbox/files?folderId=" + (parentFolder != null ? parentFolder.getId() : "");
	}

	@GetMapping("/springbox/files/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
		var optionalFile = fileRepository.findById(id);
		if (optionalFile.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		var file = optionalFile.get();
		try {
			var resource = new UrlResource(Paths.get(file.getPath()).toUri());
			if (resource.exists() || resource.isReadable()) {
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
						.body(resource);
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@GetMapping("/springbox/files/edit/{id}")
	public String editFileForm(@PathVariable Long id, Model model) throws IOException {
		var optionalFile = fileRepository.findById(id);
		if (optionalFile.isEmpty()) {
			return "error";
		}

		var file = optionalFile.get();
		var content = Files.readString(Paths.get(file.getPath()));
		file.setContent(content);

		model.addAttribute("file", file);
		return "edit-file";
	}

	@PostMapping("/springbox/files/edit")
	public String editFile(@RequestParam("id") Long id, @RequestParam("content") String content) throws IOException {
		var optionalFile = fileRepository.findById(id);
		if (optionalFile.isEmpty()) {
			return "error";
		}
		Files.writeString(Paths.get(optionalFile.get().getPath()), content);
		return "redirect:/springbox/files";
	}

	@PostMapping("/springbox/folder/delete")
	public String deleteFolder(@RequestParam("id") Long id) {
		deleteFolderAndContents(id);
		return "redirect:/springbox/files";
	}

	@PostMapping("/springbox/file/delete")
	public String deleteFile(@RequestParam("id") Long id) {
		fileRepository.deleteById(id);
		return "redirect:/springbox/files";
	}

	private void deleteFolderAndContents(Long folderId) {
		var subFolders = folderRepository.findByParentFolder(folderRepository.findById(folderId).orElse(null));
		for (var subFolder : subFolders) {
			deleteFolderAndContents(subFolder.getId());
		}
		var files = fileRepository.findByFolderAndUser(folderRepository.findById(folderId).orElse(null),
				userRepository.findById(Objects.requireNonNull(folderRepository.findById(folderId).orElse(null)).getUser().getId()).orElse(null));
		for (var file : files) {
			fileRepository.deleteById(file.getId());
		}
		folderRepository.deleteById(folderId);
	}

	private double calculateTotalSize(User user) {
		var files = fileRepository.findByUser(user);
		var totalSizeInBytes = files.stream().mapToLong(file -> {
			try {
				return Files.size(Paths.get(file.getPath()));
			} catch (IOException e) {
				return 0;
			}
		}).sum();
		return totalSizeInBytes / (1024.0 * 1024.0);
	}

	@GetMapping("/springbox/search")
	public String searchFiles(@RequestParam("query") String query, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		var optionalUser = userRepository.findByEmail(userDetails.getUsername());
		if (optionalUser.isEmpty()) {
			return "error";
		}
		var user = optionalUser.get();
		var files = fileRepository.findByUserAndNameContaining(user, query);
		for (var file : files) {
			try {
				Files.readString(Paths.get(file.getPath()));
				file.setEditable(true);
			} catch (IOException e) {
				file.setEditable(false);
			}
		}
		model.addAttribute("files", files);
		model.addAttribute("query", query);
		return "search-results";
	}
}