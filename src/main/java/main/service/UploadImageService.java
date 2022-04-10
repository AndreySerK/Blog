package main.service;

import main.DTO.LoadImageErrDto;
import main.api.response.LoadImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class UploadImageService {

    private String getRandomPath() {
        StringBuilder randomPath = new StringBuilder("upload/");
        for (int i = 1; i < 4; i++) {
            randomPath.append(UUID.randomUUID().toString(), 0, 2).append("/");
        }
        return randomPath.toString();
    }

    public ResponseEntity<?> uploadImage(MultipartFile file) throws IOException {

        LoadImageResponse loadImageResponse = new LoadImageResponse();

        if (file.getSize() > 10485760) {
            loadImageResponse.setResult(false);
            LoadImageErrDto loadImageErrDto = new LoadImageErrDto();
            loadImageErrDto.setImage("Размер файла превышает допустимый размер");
            loadImageResponse.setErrors(loadImageErrDto);
            return new ResponseEntity<>(loadImageResponse, HttpStatus.BAD_REQUEST);
        }
        if (!Objects.requireNonNull(file.getOriginalFilename()).contains("jpg")
                && !file.getOriginalFilename().contains("png")) {
            loadImageResponse.setResult(false);
            LoadImageErrDto loadImageErrDto = new LoadImageErrDto();
            loadImageErrDto.setImage("Недопустимый тип изображения");
            loadImageResponse.setErrors(loadImageErrDto);
            return new ResponseEntity<>(loadImageResponse, HttpStatus.BAD_REQUEST);
        }
        String randomPath = getRandomPath();
        new File(randomPath).mkdirs();
        BufferedImage image = ImageIO.read(file.getInputStream());
        int maxImageSize = 300;
        if (image.getWidth() > maxImageSize || image.getHeight() > maxImageSize) {
            image = Scalr.resize(image, maxImageSize, maxImageSize);
        }
        File imageFile = new File(randomPath + file.getOriginalFilename());
        ImageIO.write(image, "png", imageFile);
        return ResponseEntity.ok("/" + randomPath + file.getOriginalFilename());
    }

}
