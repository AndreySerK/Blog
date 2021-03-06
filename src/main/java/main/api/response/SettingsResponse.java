package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SettingsResponse {

    @JsonProperty("MULTIUSER_MODE")
    private Boolean multiuserMode;
    @JsonProperty("POST_PREMODERATION")
    private Boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private Boolean statisticsIsPublic;
}

