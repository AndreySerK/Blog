package main.service;

import lombok.RequiredArgsConstructor;
import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.model.enums.Value;
import main.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final GlobalSettingRepository globalSettingRepository;

    public SettingsResponse getGlobalSettings () {
        SettingsResponse settingsResponse = new SettingsResponse();
        Value multiUserModeValue = globalSettingRepository.findById(1).orElseThrow().getValue();
        Value postPremoderationValue = globalSettingRepository.findById(2).orElseThrow().getValue();
        Value statisticsIsPublicValue = globalSettingRepository.findById(3).orElseThrow().getValue();
        if (multiUserModeValue.equals(Value.YES)) {
            settingsResponse.setMultiuserMode(true);
        } else {
            settingsResponse.setMultiuserMode(false);
        }
        if (postPremoderationValue.equals(Value.YES)) {
            settingsResponse.setPostPremoderation(true);
        } else {
            settingsResponse.setPostPremoderation(false);
        }
        if (statisticsIsPublicValue.equals(Value.YES)) {
            settingsResponse.setStatisticsIsPublic(true);
        } else {
            settingsResponse.setStatisticsIsPublic(false);
        }
        return settingsResponse;
    }

    public void setGlobalSettings (SettingsRequest request) {
        GlobalSetting multiUserMode = globalSettingRepository.findById(1).orElseThrow();
        GlobalSetting postPremoderation = globalSettingRepository.findById(2).orElseThrow();
        GlobalSetting statisticsIsPublic = globalSettingRepository.findById(3).orElseThrow();
        if (request.isMultiuserMode()) {
            multiUserMode.setValue(Value.YES);
            globalSettingRepository.save(multiUserMode);
        } else {
            multiUserMode.setValue(Value.NO);
            globalSettingRepository.save(multiUserMode);
        }
        if (request.isPostPremoderation()) {
            postPremoderation.setValue(Value.YES);
            globalSettingRepository.save(postPremoderation);
        } else {
            postPremoderation.setValue(Value.NO);
            globalSettingRepository.save(postPremoderation);
        }
        if (request.isStatisticsIsPublic()) {
            statisticsIsPublic.setValue(Value.YES);
            globalSettingRepository.save(statisticsIsPublic);
        } else {
            statisticsIsPublic.setValue(Value.NO);
            globalSettingRepository.save(statisticsIsPublic);
        }
    }
}
