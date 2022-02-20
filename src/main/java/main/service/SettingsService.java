package main.service;

import lombok.RequiredArgsConstructor;
import main.api.response.SettingsResponse;
import main.model.enums.Value;
import main.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final GlobalSettingRepository globalSettingRepository;

    public SettingsResponse getGlobalSettings () {
        SettingsResponse settingsResponse = new SettingsResponse();
        Value multiUserModeValue = globalSettingRepository.findById(1).get().getValue();
        Value postPremoderationValue = globalSettingRepository.findById(2).get().getValue();
        Value statisticsIsPublicValue = globalSettingRepository.findById(3).get().getValue();
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
}
