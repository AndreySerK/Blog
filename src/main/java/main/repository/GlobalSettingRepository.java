package main.repository;

import main.model.GlobalSetting;
import main.model.enums.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

    GlobalSetting findByCode(Code code);
}
