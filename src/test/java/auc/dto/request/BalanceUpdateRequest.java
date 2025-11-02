package auc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceUpdateRequest {
    @JsonProperty("amount")
    private Double amount;

    public static Builder builder() {
        return new Builder();
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public static class Builder {
        private Double amount;

        public Builder amount(Double amount) { 
            this.amount = amount; 
            return this; 
        }

        public BalanceUpdateRequest build() {
            BalanceUpdateRequest request = new BalanceUpdateRequest();
            request.amount = this.amount;
            return request;
        }
    }
}
