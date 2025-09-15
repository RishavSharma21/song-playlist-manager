package com.musicapp.songplaylistmanager.service;


import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.repository.SongRepository;
import com.musicapp.songplaylistmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    // Create new song (Admin only)
    public Song createSong(Song song, String adminUserId) {
        logger.info("🎵 Creating new song: {} by {}", song.getTitle(), song.getArtist());

        // Verify admin privileges
        Optional<User> admin = userRepository.findById(adminUserId);
        if (admin.isEmpty() || !admin.get().isAdmin()) {
            logger.error("❌ Unauthorized: Only admins can create songs");
            throw new RuntimeException("Only admins can create songs");
        }

        Song savedSong = songRepository.save(song);
        logger.info("✅ Song created successfully with ID: {}", savedSong.getId());

        return savedSong;
    }

    // Get all songs
    public List<Song> getAllSongs() {
        logger.info("📋 Fetching all songs from database");
        List<Song> songs = songRepository.findAll();
        logger.info("📊 Found {} songs", songs.size());
        return songs;
    }

    // Get song by ID
    public Optional<Song> getSongById(String id) {
        logger.info("🔍 Searching for song with ID: {}", id);
        Optional<Song> song = songRepository.findById(id);

        if (song.isPresent()) {
            logger.info("✅ Song found: {}", song.get().getTitle());
        } else {
            logger.warn("❌ Song not found with ID: {}", id);
        }

        return song;
    }

    // Update song (Admin only)
    public Song updateSong(String id, Song updatedSong, String adminUserId) {
        logger.info("🔄 Updating song with ID: {}", id);

        // Verify admin privileges
        Optional<User> admin = userRepository.findById(adminUserId);
        if (admin.isEmpty() || !admin.get().isAdmin()) {
            logger.error("❌ Unauthorized: Only admins can update songs");
            throw new RuntimeException("Only admins can update songs");
        }

        Optional<Song> existingSong = songRepository.findById(id);
        if (existingSong.isEmpty()) {
            logger.error("❌ Song not found with ID: {}", id);
            throw new RuntimeException("Song not found");
        }

        Song song = existingSong.get();
        song.setTitle(updatedSong.getTitle());
        song.setArtist(updatedSong.getArtist());
        song.setAlbum(updatedSong.getAlbum());
        song.setGenre(updatedSong.getGenre());
        song.setDuration(updatedSong.getDuration());

        Song savedSong = songRepository.save(song);
        logger.info("✅ Song updated successfully: {}", savedSong.getTitle());

        return savedSong;
    }

    // Delete song (Admin only)
    public void deleteSong(String id, String adminUserId) {
        logger.info("🗑️ Deleting song with ID: {}", id);

        // Verify admin privileges
        Optional<User> admin = userRepository.findById(adminUserId);
        if (admin.isEmpty() || !admin.get().isAdmin()) {
            logger.error("❌ Unauthorized: Only admins can delete songs");
            throw new RuntimeException("Only admins can delete songs");
        }

        Optional<Song> song = songRepository.findById(id);
        if (song.isEmpty()) {
            logger.error("❌ Song not found with ID: {}", id);
            throw new RuntimeException("Song not found");
        }

        songRepository.deleteById(id);
        logger.info("✅ Song deleted successfully: {}", song.get().getTitle());
    }

    // Like/Unlike song
    public Song toggleLikeSong(String songId, String userId) {
        logger.info("❤️ Toggling like for song ID: {} by user: {}", songId, userId);

        Optional<Song> songOpt = songRepository.findById(songId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (songOpt.isEmpty()) {
            logger.error("❌ Song not found with ID: {}", songId);
            throw new RuntimeException("Song not found");
        }

        if (userOpt.isEmpty()) {
            logger.error("❌ User not found with ID: {}", userId);
            throw new RuntimeException("User not found");
        }

        Song song = songOpt.get();
        User user = userOpt.get();

        if (user.hasLikedSong(songId)) {
            // Unlike the song
            user.unlikeSong(songId);
            song.decrementLikeCount();
            logger.info("💔 User unliked song: {}", song.getTitle());
        } else {
            // Like the song
            user.likeSong(songId);
            song.incrementLikeCount();
            logger.info("❤️ User liked song: {}", song.getTitle());
        }

        userRepository.save(user);
        Song savedSong = songRepository.save(song);
        logger.info("✅ Like status updated. Current likes: {}", savedSong.getLikeCount());

        return savedSong;
    }

    // Search songs
    public List<Song> searchSongs(String query) {
        logger.info("🔍 Searching songs with query: {}", query);
        List<Song> songs = songRepository.searchByTitleOrArtist(query);
        logger.info("📊 Found {} songs matching query", songs.size());
        return songs;
    }

    // Get most liked songs
    public List<Song> getMostLikedSongs() {
        logger.info("🏆 Fetching most liked songs");
        List<Song> songs = songRepository.findTop10ByOrderByLikeCountDesc();
        logger.info("📊 Retrieved {} most liked songs", songs.size());
        return songs;
    }

    // Get songs by genre
    public List<Song> getSongsByGenre(String genre) {
        logger.info("🎼 Fetching songs by genre: {}", genre);
        List<Song> songs = songRepository.findByGenreIgnoreCase(genre);
        logger.info("📊 Found {} songs in genre: {}", songs.size(), genre);
        return songs;
    }
}