package com.example.hearurbackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class S3Uploader {
    private final AmazonS3 amazonS3;
    private final String bucket;
    private final String bucketPath;

    public S3Uploader(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket, @Value("${s3.bucket.path}") String bucketPath) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.bucketPath= bucketPath;
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 변환 실패"));
        int fileNumber = 1;

        // 적절한 파일 번호 찾기
        while (checkFileExists(dirName, fileNumber)) {
            fileNumber++;
        }

        String fileName = bucketPath + dirName + "/" + fileNumber;  // 최종 파일 이름 구성
        return upload(file, dirName, fileName);
    }
    private boolean checkFileExists(String dirName, int fileNumber) {
        String fileName = dirName + "/" + fileNumber;
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket).withPrefix(fileName);
        ListObjectsV2Result result = amazonS3.listObjectsV2(req);

        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            if (objectSummary.getKey().equals(fileName)) {
                return true;  // 파일이 존재함
            }
        }
        return false;  // 파일이 존재하지 않음
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    public String upload(File file, String dirName, String fileName) {
        String uploadImageUrl = putS3(file, fileName);
        removeNewFile(file);
        return uploadImageUrl;
    }

    private String putS3(File file, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File file) {
        if (file.delete()) {
            log.info("파일이 삭제되었습니다.");
            return;
        }
        log.info("파일이 삭제되지 못했습니다.");
    }
}
