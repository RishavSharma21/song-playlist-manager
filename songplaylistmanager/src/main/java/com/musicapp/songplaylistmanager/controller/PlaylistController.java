//package com.musicapp.songplaylistmanager.controller;
//
//
//
//import com.musicapp.songplaylistmanager.model.Song.Playlist;
//import com.musicapp.songplaylistmanager.service.PlaylistService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import jakarta.validation.Valid;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/playlists")
//public class PlaylistController {
//
//    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);
//
//    @Autowired
//    private PlaylistService playlistService;
//
//    // Create playlist
//    @PostMapping
//    public ResponseEntity<?> createPlaylist(@Valid @RequestBody Playlist playlist) {
//        try {
//            logger.info("📝 API: Creating playlist: {} for user: {}",
//                    playlist.getName(), playlist.getUserId());
//            Playlist createdPlaylist = playlistService.createPlaylist(playlist);
//            logger.info("✅ API: Playlist created successfully with ID: {}",
//                    createdPlaylist.getId());
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylist);
//        } catch (Exception e) {
//            logger.error("❌ API: Error creating playlist: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    // Get playlist by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getPlaylistById(@PathVariable String id) {
//        logger.info("🔍 API: Fetching playlist with ID: {}", id);
//        Optional<Playlist> playlist = playlistService.getPlaylistById(id);
//
//        if (playlist.isPresent()) {
//            logger.info("✅ API: Playlist found: {}", playlist.get().getName());
//            return ResponseEntity.ok(playlist.get());
//        } else {
//            logger.warn("❌ API: Playlist not found with ID: {}", id);
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // Get user's playlists
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getUserPlaylists(@PathVariable String userId) {
//        try {
//            logger.info("📋 API: Fetching playlists for user ID: {}", userId);
//            List<Playlist> playlists = playlistService.getUserPlaylists(userId);
//            logger.info("✅ API: Retrieved {} playlists for user", playlists.size());
//            return ResponseEntity.ok(playlists);
//        } catch (Exception e) {
//            logger.error("❌ API: Error fetching user playlists: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    // Update playlist
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updatePlaylist(@PathVariable String id,
//                                            @Valid @RequestBody Playlist playlist,
//                                            @RequestParam String userId) {
//        try {
//            logger.info("🔄 API: Updating playlist with ID: {}", id);
//            Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlist, userId);
//            logger.info("✅ API: Playlist updated successfully: {}",
//                    updatedPlaylist.getName());
//            return ResponseEntity.ok(updatedPlaylist);
//        } catch (Exception e) {
//            logger.error("❌ API: Error updating playlist: {}", e.getMessage());
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Error: " + e.getMessage());
//        }
//    }
//
//    // Delete playlist
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePlaylist(@PathVariable String id,
//                                            @RequestParam String userId) {
//        try {
//            logger.info("🗑️ API: Deleting playlist with ID: {}", id);
//            playlistService.deletePlaylist(id, userId);
//            logger.info("✅ API: Playlist deleted successfully");
//            return ResponseEntity.ok("Playlist deleted successfully");
//        } catch (Exception e) {
//            logger.error("❌ API: Error deleting playlist: {}", e.getMessage());
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Error: " + e.getMessage());
//        }
//    }
//
//    // Add song to playlist
//    @PostMapping("/{playlistId}/songs/{songId}")
//    public ResponseEntity<?> addSongToPlaylist(@PathVariable String playlistId,
//                                               @PathVariable String songId,
//                                               @RequestParam String userId) {
//        try {
//            logger.info("➕ API: Adding song {} to playlist {}", songId, playlistId);
//            Playlist updatedPlaylist = playlistService.addSongToPlaylist(playlistId, songId, userId);
//            logger.info("✅ API: Song added successfully. Total songs: {}",
//                    updatedPlaylist.getSongCount());
//            return ResponseEntity.ok(updatedPlaylist);
//        } catch (Exception e) {
//            logger.error("❌ API: Error adding song to playlist: {}", e.getMessage());
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Error: " + e.getMessage());
//        }
//    }
//
//    // Remove song from playlist
//    @DeleteMapping("/{playlistId}/songs/{songId}")
//    public ResponseEntity<?> removeSongFromPlaylist(@PathVariable String playlistId,
//                                                    @PathVariable String songId,
//                                                    @RequestParam String userId) {
//        try {
//            logger.info("➖ API: Removing song {} from playlist {}", songId, playlistId);
//            Playlist updatedPlaylist = playlistService.removeSongFromPlaylist(playlistId, songId, userId);
//            logger.info("✅ API: Song removed successfully. Remaining songs: {}",
//                    updatedPlaylist.getSongCount());
//            return ResponseEntity.ok(updatedPlaylist);
//        } catch (Exception e) {
//            logger.error("❌ API: Error removing song from playlist: {}", e.getMessage());
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Error: " + e.getMessage());
//        }
//    }
//
//    // Get all playlists (for admin)
//    @GetMapping
//    public ResponseEntity<List<Playlist>> getAllPlaylists() {
//        logger.info("📋 API: Fetching all playlists");
//        List<Playlist> playlists = playlistService.getAllPlaylists();
//        logger.info("✅ API: Retrieved {} playlists", playlists.size());
//        return ResponseEntity.ok(playlists);
//    }
//}
