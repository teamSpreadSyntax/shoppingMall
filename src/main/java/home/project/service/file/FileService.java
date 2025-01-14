package home.project.service.file;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final Storage storage; // GCS Storage 객체 주입

    @Value("${google.cloud.storage.bucket-name}")
    private String bucketName;

    @Value("${file.allowed-extensions}")
    private List<String> allowedExtensions;

    public String saveFile(MultipartFile file, String domain, String userId) {
        try {
            // 파일 확장자 검사
            String extension = getFileExtension(file.getOriginalFilename());
            log.info("File Extension: {}", extension);
            log.info("Allowed Extensions: {}", allowedExtensions);

            if (!allowedExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID() + "." + extension;

            // 도메인 + 사용자 ID 경로 설정
            String folderPath = String.format("%s/%s/%s", domain, userId, LocalDate.now());
            String fullPath = folderPath + "/" + fileName;

            log.info("Generated File Path: {}", fullPath);

            // GCS 버킷에 파일 업로드
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fullPath).build();
            storage.create(blobInfo, file.getBytes());
            log.info("File uploaded to GCS: {}", fullPath);

            // GCS URL 반환
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fullPath);

        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 확장자 추출
     *
     * @param fileName 파일 이름
     * @return 파일 확장자
     */
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
