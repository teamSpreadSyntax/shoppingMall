package home.project.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // 로깅 추가
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private List<String> allowedExtensions;

    public String saveFile(MultipartFile file) {
        try {
            // 현재 작업 디렉토리 로깅
            log.info("Current Working Directory: {}", System.getProperty("user.dir"));
            log.info("Upload Directory: {}", uploadDir);

            // 파일 확장자 검사
            String extension = getFileExtension(file.getOriginalFilename());
            log.info("File Extension: {}", extension);
            log.info("Allowed Extensions: {}", allowedExtensions);

            if (!allowedExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            // 파일명 생성
            String fileName = UUID.randomUUID().toString() + "." + extension;
            log.info("Generated File Name: {}", fileName);

            // 저장 경로 생성
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            log.info("Absolute Upload Path: {}", uploadPath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Directory created: {}", uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            log.info("Full File Path: {}", filePath);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved successfully");

            // URL 형식으로 반환
            return "/images/products/" + fileName;

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            log.warn("Filename is null or empty");
            return "";
        }
        int dot = fileName.lastIndexOf(".");
        if (dot < 0) {
            log.warn("No extension found in filename: {}", fileName);
            return "";
        }
        return fileName.substring(dot + 1);
    }
}