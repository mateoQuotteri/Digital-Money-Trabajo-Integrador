package com.user_service.Dtos;

public class LoginResponse {
    private String accessToken;
    private UserDto user;

    public LoginResponse() {}

    public LoginResponse(String accessToken, UserDto user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Getters y setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    // Clase interna para user
    public static class UserDto {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;

        public UserDto(Long id, String email, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
}