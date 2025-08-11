package com.uade.tpo.demo.models.responses;

public class UserDTOReduced {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    // Constructors
    public UserDTOReduced() {}

    public UserDTOReduced(Long id, String username, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // Builder pattern
    public static UserDTOReducedBuilder builder() {
        return new UserDTOReducedBuilder();
    }

    public static class UserDTOReducedBuilder {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;

        public UserDTOReducedBuilder id(Long id) { this.id = id; return this; }
        public UserDTOReducedBuilder username(String username) { this.username = username; return this; }
        public UserDTOReducedBuilder email(String email) { this.email = email; return this; }
        public UserDTOReducedBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserDTOReducedBuilder lastName(String lastName) { this.lastName = lastName; return this; }

        public UserDTOReduced build() {
            return new UserDTOReduced(id, username, email, firstName, lastName);
        }
    }
}
