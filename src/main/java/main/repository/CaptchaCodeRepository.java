package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    List<CaptchaCode> findCaptchaCodeBySecretCode (String secretCode);
    CaptchaCode getCaptchaCodeBySecretCode (String secretCode);
}
