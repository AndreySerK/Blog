package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.DTO.CaptchaCodeDto;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;
import java.util.Date;


@Service
public class CaptchaCodeService {

    private final CaptchaCodeRepository codeRepository;

    public CaptchaCodeService(CaptchaCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public CaptchaCodeDto getCaptchaCodeDto () {
        Cage cage = new GCage();
        CaptchaCode captchaCode = new CaptchaCode();
        CaptchaCodeDto captchaCodeDto = new CaptchaCodeDto();
        String code = cage.getTokenGenerator().next();
        try {
            String secretCode = new RandomStringGenerator.Builder().withinRange(30,122).build().generate(12);
            captchaCode.setCode(code);
            captchaCode.setSecretCode(secretCode);
            captchaCode.setTime(new Date());
            codeRepository.save(captchaCode);
            BufferedImage image = Scalr.resize(cage.drawImage(code), 150, 50);
            captchaCodeDto.setSecret(secretCode);
            File imageFile = new File("image.png");
            ImageIO.write(image, "png", imageFile);
            byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            captchaCodeDto.setImage( "data:image/png;base64, " + encodedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        removeOutdatedCaptchaCode(3600000);
        return captchaCodeDto;
    }

    public void removeOutdatedCaptchaCode (int timePeriodInMs) {
        Date date = new Date(System.currentTimeMillis() - timePeriodInMs);
        codeRepository.findAll()
                .forEach(captchaCode -> {
                    if (captchaCode.getTime().before(date)) {
                        codeRepository.delete(captchaCode);
                    }
                });
    }

}
