package cn.diodecn.MyOKR.demo.controller;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("Storage").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建用于存储文件的目录。", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        // 首先，生成文件的哈希值
        String hash = sha1Hash(file.getBytes());
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String hashedFileName = hash + (fileExtension != null ? "." + fileExtension : "");

        Path targetLocation = this.fileStorageLocation.resolve(hashedFileName);

        // 检查文件是否已存在
        if (!Files.exists(targetLocation)) {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return hashedFileName;
    }

    private String sha1Hash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] result = sha1.digest(input);
        return byteArray2Hex(result);
    }

    private String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = storeFile(file);
            return ResponseEntity.ok("文件上传成功：" + fileName);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("无法存储文件。请再试一次！");
        } catch (NoSuchAlgorithmException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("生成文件哈希失败");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败：" + ex.getMessage());
        }
    }
}
