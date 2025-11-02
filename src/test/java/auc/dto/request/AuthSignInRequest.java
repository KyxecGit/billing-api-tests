package auc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthSignInRequest {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static class Builder {
        private String username;
        private String password;

        public Builder username(String username) { 
            this.username = username; 
            return this; 
        }

        public Builder password(String password) { 
            this.password = password; 
            return this; 
        }

        public AuthSignInRequest build() {
            AuthSignInRequest request = new AuthSignInRequest();
            request.username = this.username;
            request.password = this.password;
            return request;
        }
    }
}
