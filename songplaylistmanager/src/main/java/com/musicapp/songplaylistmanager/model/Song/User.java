package com.musicapp.songplaylistmanager.model.Song;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Indexed(unique = true)
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private List<String> likedSongs; // Song IDs

    private LocalDateTime createdDate;

    // Constructors
    public User() {
        this.createdDate = LocalDateTime.now();
        this.likedSongs = new ArrayList<>();
        this.role = Role.USER; // Default role
    }

    public User(String username, String email, Role role) {
        this();
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<String> getLikedSongs() { return likedSongs; }
    public void setLikedSongs(List<String> likedSongs) { this.likedSongs = likedSongs; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    // Utility methods
    public void likeSong(String songId) {
        if (!likedSongs.contains(songId)) {
            likedSongs.add(songId);
        }
    }

    public void unlikeSong(String songId) {
        likedSongs.remove(songId);
    }

    public boolean hasLikedSong(String songId) {
        return likedSongs.contains(songId);
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", likedSongsCount=" + likedSongs.size() +
                '}';
    }
}