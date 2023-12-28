package cn.diodecn.MyOKR.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileShareController {

    private final Path fileStorageLocation;

    public FileShareController() {
        this.fileStorageLocation = Paths.get("Storage").toAbsolutePath().normalize();
    }

    @GetMapping("/share/{filename:.+}")
    public ResponseEntity<Resource> shareFile(@PathVariable String filename) {
        try {
            Path file = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok(resource);
            } else {
                throw new RuntimeException("无法读取文件: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法共享文件: " + filename, e);
        }
    }
}
