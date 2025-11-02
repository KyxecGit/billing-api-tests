package auc.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("profileId")
    private Long profileId;

    @JsonProperty("megabytes")
    private Long megabytes;

    @JsonProperty("seconds")
    private Long seconds;

    @JsonProperty("sms")
    private Integer sms;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public Long getMegabytes() { return megabytes; }
    public void setMegabytes(Long megabytes) { this.megabytes = megabytes; }

    public Long getSeconds() { return seconds; }
    public void setSeconds(Long seconds) { this.seconds = seconds; }

    public Integer getSms() { return sms; }
    public void setSms(Integer sms) { this.sms = sms; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
