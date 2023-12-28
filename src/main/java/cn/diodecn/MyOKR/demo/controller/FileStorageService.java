package cn.diodecn.MyOKR.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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


    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = createUniqueFileName(originalFileName);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("无法存储文件 " + fileName + "。请再试一次！", ex);
        }
    }

    private String createUniqueFileName(String originalFileName) {
        // 提取文件扩展名，包括点号
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i); // 包含点号
        }

        // 如果文件名没有扩展名，就在这里处理
        if (fileExtension.isEmpty()) {
            // 处理没有扩展名的情况（如果需要）
            // 比如，你可以设置一个默认的扩展名或者其他逻辑
        }

        String baseName = originalFileName.substring(0, i >= 0 ? i : originalFileName.length());

        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String hash = sha1Hash(baseName);

        return dateString + "_" + hash + fileExtension; // 确保扩展名被包含
    }


    private String sha1Hash(String input) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return byteArray2Hex(sha1.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法生成文件哈希", e);
        }
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
            FileStorageService fileStorageService = new FileStorageService();
            String fileName = fileStorageService.storeFile(file);
            return ResponseEntity.ok("文件上传成功: " + fileName);
        } catch (RuntimeException ex) {
            // 检查异常类型并返回相应的状态码
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("无法存储文件，创建目录失败");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
        }
    }


}
