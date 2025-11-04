package vn.project.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import vn.project.jobhunter.domain.file.ResUpLoadFileDTO;
import vn.project.jobhunter.service.FileService;
import vn.project.jobhunter.util.anotation.ApiMessage;
import vn.project.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1")

public class FileController {
    private final FileService fileService;
        @Value("${tuna.upload-file.base-uri}")
    private String baseURI;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUpLoadFileDTO> upload(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("folder") String folder) throws StorageException,URISyntaxException, IOException{
          if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty.");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        boolean isValidFile = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValidFile) {
            throw new StorageException("Invalid file. Only allow: " + allowedExtensions.toString());
        }

        this.fileService.createDirectory(baseURI + folder);
        String uploadFile = this.fileService.store(file, folder);
        ResUpLoadFileDTO res=new ResUpLoadFileDTO(uploadFile, java.time.Instant.now());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download file")
    public ResponseEntity<Resource> download(@RequestParam(name = "filename", required = false) String filename,
    @RequestParam(name = "folder",required = false) String folder) throws StorageException, IOException, URISyntaxException{
         if (filename == null || filename.isEmpty()) {
            throw new StorageException("Filename is empty.");
        }

        // check file exists(and not a directory)
        long fileLength=this.fileService.getFileLength(filename, folder);
        if (fileLength == 0) {
            throw new StorageException("File not found.");
        }

        //download file
        InputStreamResource resource = this.fileService.getResource(filename, folder);
        return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+filename +"\"")
                            .contentLength(fileLength)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(resource);
    }




}
