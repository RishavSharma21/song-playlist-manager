package com.musicapp.songplaylistmanager.model.Song;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "playlists")
public class Playlist {

    @Id
    private String id;

    @NotBlank(message = "Playlist name is required")
    @Size(min = 1, max = 50, message = "Playlist name must be between 1 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotBlank(message = "User ID is required")
    private String userId; // Reference to User

    private List<String> songIds; // References to Songs

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // Constructors
    public Playlist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.songIds = new ArrayList<>();
    }

    public Playlist(String name, String description, String userId) {
        this();
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.updatedDate = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.updatedDate = LocalDateTime.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getSongIds() { return songIds; }
    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
        this.updatedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    // Utility methods
    public void addSong(String songId) {
        if (!songIds.contains(songId)) {
            songIds.add(songId);
            this.updatedDate = LocalDateTime.now();
        }
    }

    public void removeSong(String songId) {
        if (songIds.remove(songId)) {
            this.updatedDate = LocalDateTime.now();
        }
    }

    public boolean containsSong(String songId) {
        return songIds.contains(songId);
    }

    public int getSongCount() {
        return songIds.size();
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", songCount=" + songIds.size() +
                ", createdDate=" + createdDate +
                '}';
    }
}