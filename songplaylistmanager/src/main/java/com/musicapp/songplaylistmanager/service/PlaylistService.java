package com.musicapp.songplaylistmanager.service;


import com.musicapp.songplaylistmanager.model.Song.Playlist;
import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.repository.PlaylistRepository;
import com.musicapp.songplaylistmanager.repository.SongRepository;
import com.musicapp.songplaylistmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    // Create new playlist
    public Playlist createPlaylist(Playlist playlist) {
        logger.info("📝 Creating new playlist: {} for user: {}", playlist.getName(), playlist.getUserId());

        Optional<User> user = userRepository.findById(playlist.getUserId());
        if (user.isEmpty()) {
            logger.error("❌ User not found with ID: {}", playlist.getUserId());
            throw new RuntimeException("User not found");
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);
        logger.info("✅ Playlist created successfully with ID: {}", savedPlaylist.getId());

        return savedPlaylist;
    }

    // Get playlist by ID
    public Optional<Playlist> getPlaylistById(String id) {
        logger.info("🔍 Searching for playlist with ID: {}", id);
        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (playlist.isPresent()) {
            logger.info("✅ Playlist found: {}", playlist.get().getName());
        } else {
            logger.warn("❌ Playlist not found with ID: {}", id);
        }

        return playlist;
    }

    // Get all playlists for a user
    public List<Playlist> getUserPlaylists(String userId) {
        logger.info("📋 Fetching playlists for user ID: {}", userId);

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            logger.error("❌ User not found with ID: {}", userId);
            throw new RuntimeException("User not found");
        }

        List<Playlist> playlists = playlistRepository.findByUserId(userId);
        logger.info("📊 Found {} playlists for user", playlists.size());
        return playlists;
    }

    // Update playlist
    public Playlist updatePlaylist(String playlistId, Playlist updatedPlaylist, String userId) {
        logger.info("🔄 Updating playlist with ID: {}", playlistId);

        Optional<Playlist> existingPlaylistOpt = playlistRepository.findById(playlistId);
        if (existingPlaylistOpt.isEmpty()) {
            logger.error("❌ Playlist not found with ID: {}", playlistId);
            throw new RuntimeException("Playlist not found");
        }

        Playlist playlist = existingPlaylistOpt.get();

        // Verify ownership
        if (!playlist.getUserId().equals(userId)) {
            logger.error("❌ Unauthorized: User {} cannot update playlist owned by {}", userId, playlist.getUserId());
            throw new RuntimeException("You can only update your own playlists");
        }

        playlist.setName(updatedPlaylist.getName());
        playlist.setDescription(updatedPlaylist.getDescription());

        Playlist savedPlaylist = playlistRepository.save(playlist);
        logger.info("✅ Playlist updated successfully: {}", savedPlaylist.getName());

        return savedPlaylist;
    }

    // Delete playlist
    public void deletePlaylist(String playlistId, String userId) {
        logger.info("🗑️ Deleting playlist with ID: {}", playlistId);

        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isEmpty()) {
            logger.error("❌ Playlist not found with ID: {}", playlistId);
            throw new RuntimeException("Playlist not found");
        }

        Playlist playlist = playlistOpt.get();

        // Verify ownership
        if (!playlist.getUserId().equals(userId)) {
            logger.error("❌ Unauthorized: User {} cannot delete playlist owned by {}", userId, playlist.getUserId());
            throw new RuntimeException("You can only delete your own playlists");
        }

        playlistRepository.deleteById(playlistId);
        logger.info("✅ Playlist deleted successfully: {}", playlist.getName());
    }

    // Add song to playlist
    public Playlist addSongToPlaylist(String playlistId, String songId, String userId) {
        logger.info("➕ Adding song {} to playlist {}", songId, playlistId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.error("❌ Playlist not found with ID: {}", playlistId);
                    return new RuntimeException("Playlist not found");
                });

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.error("❌ Song not found with ID: {}", songId);
                    return new RuntimeException("Song not found");
                });

        // Verify ownership
        if (!playlist.getUserId().equals(userId)) {
            logger.error("❌ Unauthorized: User {} cannot modify playlist owned by {}", userId, playlist.getUserId());
            throw new RuntimeException("You can only modify your own playlists");
        }

        if (playlist.containsSong(songId)) {
            logger.warn("⚠️ Song already exists in playlist: {}", song.getTitle());
            throw new RuntimeException("Song already exists in playlist");
        }

        playlist.addSong(songId);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        logger.info("✅ Song '{}' added to playlist '{}'. Total songs: {}", song.getTitle(), playlist.getName(), savedPlaylist.getSongCount());

        return savedPlaylist;
    }

    // Remove song from playlist
    public Playlist removeSongFromPlaylist(String playlistId, String songId, String userId) {
        logger.info("➖ Removing song {} from playlist {}", songId, playlistId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.error("❌ Playlist not found with ID: {}", playlistId);
                    return new RuntimeException("Playlist not found");
                });

        // Verify ownership
        if (!playlist.getUserId().equals(userId)) {
            logger.error("❌ Unauthorized: User {} cannot modify playlist owned by {}", userId, playlist.getUserId());
            throw new RuntimeException("You can only modify your own playlists");
        }

        if (!playlist.containsSong(songId)) {
            logger.warn("⚠️ Song not found in playlist");
            throw new RuntimeException("Song not found in playlist");
        }

        playlist.removeSong(songId);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        logger.info("✅ Song removed from playlist '{}'. Remaining songs: {}", playlist.getName(), savedPlaylist.getSongCount());

        return savedPlaylist;
    }

    // Get all playlists (Admin only)
    public List<Playlist> getAllPlaylists() {
        logger.info("📋 Fetching all playlists");
        List<Playlist> playlists = playlistRepository.findAll();
        logger.info("📊 Found {} playlists", playlists.size());
        return playlists;
    }
}


