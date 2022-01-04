package main.model.repository;

import main.model.CaptchaCode;
import org.springframework.data.repository.CrudRepository;

public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {
}
