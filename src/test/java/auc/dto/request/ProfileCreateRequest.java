package auc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileCreateRequest {
    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("pricePlanId")
    private Long pricePlanId;

    public static Builder builder() {
        return new Builder();
    }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getPricePlanId() { return pricePlanId; }
    public void setPricePlanId(Long pricePlanId) { this.pricePlanId = pricePlanId; }

    public static class Builder {
        private String msisdn;
        private Long userId;
        private Long pricePlanId;

        public Builder msisdn(String msisdn) { 
            this.msisdn = msisdn; 
            return this; 
        }

        public Builder userId(Long userId) { 
            this.userId = userId; 
            return this; 
        }

        public Builder pricePlanId(Long pricePlanId) { 
            this.pricePlanId = pricePlanId; 
            return this; 
        }

        public ProfileCreateRequest build() {
            ProfileCreateRequest request = new ProfileCreateRequest();
            request.msisdn = this.msisdn;
            request.userId = this.userId;
            request.pricePlanId = this.pricePlanId;
            return request;
        }
    }
}
