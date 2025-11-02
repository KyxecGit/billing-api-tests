package auc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthSignUpRequest {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("telegramChatId")
    private String telegramChatId;

    public static Builder builder() { return new Builder(); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }

    public static class Builder {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String telegramChatId;

        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder telegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; return this; }

        public AuthSignUpRequest build() {
            AuthSignUpRequest request = new AuthSignUpRequest();
            request.username = this.username;
            request.password = this.password;
            request.firstName = this.firstName;
            request.lastName = this.lastName;
            request.telegramChatId = this.telegramChatId;
            return request;
        }
    }
}
