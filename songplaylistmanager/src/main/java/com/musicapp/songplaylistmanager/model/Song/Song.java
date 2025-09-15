package com.musicapp.songplaylistmanager.model.Song;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Document(collection = "songs")
public class Song {

    @Id
    private String id;

    @NotBlank(message = "Song title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank(message = "Artist name is required")
    @Size(min = 1, max = 50, message = "Artist name must be between 1 and 50 characters")
    private String artist;

    @Size(max = 50, message = "Album name must not exceed 50 characters")
    private String album;

    @Size(max = 30, message = "Genre must not exceed 30 characters")
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 second")
    @Max(value = 3600, message = "Duration cannot exceed 1 hour")
    private Integer duration; // in seconds

    private LocalDateTime createdDate;

    @Min(value = 0, message = "Like count cannot be negative")
    private Integer likeCount;

    // Constructors
    public Song() {
        this.createdDate = LocalDateTime.now();
        this.likeCount = 0;
    }

    public Song(String title, String artist, String album, String genre, Integer duration) {
        this();
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    // Utility methods
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", likeCount=" + likeCount +
                '}';
    }
}
