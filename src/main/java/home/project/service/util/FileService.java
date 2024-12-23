package home.project.service.util;

import lombok.RequiredArgsConstructor;
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
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private List<String> allowedExtensions;

    public String saveFile(MultipartFile file) {
        try {
            // 파일 확장자 검사
            String extension = getFileExtension(file.getOriginalFilename());
            if (!allowedExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            // 파일명 생성
            String fileName = UUID.randomUUID().toString() + "." + extension;

            // 저장 경로 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    private String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot < 0) {
            return "";
        }
        return fileName.substring(dot + 1);
    }
}