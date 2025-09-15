package com.musicapp.songplaylistmanager.controller;



import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    @Autowired
    private SongService songService;

    // Create song (Admin only)
    @PostMapping
    public ResponseEntity<?> createSong(@Valid @RequestBody Song song,
                                        @RequestParam String adminUserId) {
        try {
            logger.info("🎵 API: Creating song - Title: {}, Artist: {}", song.getTitle(), song.getArtist());
            Song createdSong = songService.createSong(song, adminUserId);
            logger.info("✅ API: Song created successfully with ID: {}", createdSong.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSong);
        } catch (Exception e) {
            logger.error("❌ API: Error creating song: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        }
    }

    // Get all songs
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        logger.info("📋 API: Fetching all songs");
        List<Song> songs = songService.getAllSongs();
        logger.info("✅ API: Retrieved {} songs", songs.size());
        return ResponseEntity.ok(songs);
    }

    // Get song by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(@PathVariable String id) {
        logger.info("🔍 API: Fetching song with ID: {}", id);
        Optional<Song> song = songService.getSongById(id);

        if (song.isPresent()) {
            logger.info("✅ API: Song found: {}", song.get().getTitle());
            return ResponseEntity.ok(song.get());
        } else {
            logger.warn("❌ API: Song not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Update song (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSong(@PathVariable String id,
                                        @Valid @RequestBody Song song,
                                        @RequestParam String adminUserId) {
        try {
            logger.info("🔄 API: Updating song with ID: {}", id);
            Song updatedSong = songService.updateSong(id, song, adminUserId);
            logger.info("✅ API: Song updated successfully: {}", updatedSong.getTitle());
            return ResponseEntity.ok(updatedSong);
        } catch (Exception e) {
            logger.error("❌ API: Error updating song: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        }
    }

    // Delete song (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable String id,
                                        @RequestParam String adminUserId) {
        try {
            logger.info("🗑️ API: Deleting song with ID: {}", id);
            songService.deleteSong(id, adminUserId);
            logger.info("✅ API: Song deleted successfully");
            return ResponseEntity.ok("Song deleted successfully");
        } catch (Exception e) {
            logger.error("❌ API: Error deleting song: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: " + e.getMessage());
        }
    }

    // Like/Unlike song
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLikeSong(@PathVariable String id,
                                            @RequestParam String userId) {
        try {
            logger.info("❤️ API: Toggling like for song ID: {} by user: {}", id, userId);
            Song song = songService.toggleLikeSong(id, userId);
            logger.info("✅ API: Like toggled. Current likes: {}", song.getLikeCount());
            return ResponseEntity.ok(song);
        } catch (Exception e) {
            logger.error("❌ API: Error toggling like: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Search songs
    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String query) {
        logger.info("🔍 API: Searching songs with query: {}", query);
        List<Song> songs = songService.searchSongs(query);
        logger.info("✅ API: Found {} songs matching query", songs.size());
        return ResponseEntity.ok(songs);
    }

    // Get most liked songs
    @GetMapping("/popular")
    public ResponseEntity<List<Song>> getMostLikedSongs() {
        logger.info("🏆 API: Fetching most liked songs");
        List<Song> songs = songService.getMostLikedSongs();
        logger.info("✅ API: Retrieved {} popular songs", songs.size());
        return ResponseEntity.ok(songs);
    }

    // Get songs by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Song>> getSongsByGenre(@PathVariable String genre) {
        logger.info("🎼 API: Fetching songs by genre: {}", genre);
        List<Song> songs = songService.getSongsByGenre(genre);
        logger.info("✅ API: Found {} songs in genre: {}", songs.size(), genre);
        return ResponseEntity.ok(songs);
    }
}
