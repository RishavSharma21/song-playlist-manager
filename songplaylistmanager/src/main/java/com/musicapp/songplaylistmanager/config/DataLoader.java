package com.musicapp.songplaylistmanager.config;




import com.musicapp.songplaylistmanager.model.Song.Playlist;
import com.musicapp.songplaylistmanager.model.Song.Role;
import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.repository.PlaylistRepository;
import com.musicapp.songplaylistmanager.repository.SongRepository;
import com.musicapp.songplaylistmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🌱 Loading sample data...");

        // Clear existing data for demo
        songRepository.deleteAll();
        userRepository.deleteAll();
        playlistRepository.deleteAll();

        // Create Admin User
        User admin = new User("admin", "admin@musicapp.com", Role.ADMIN);
        admin = userRepository.save(admin);
        logger.info("🛡️ Created admin user: {}", admin.getUsername());

        // Create Regular Users
        User user1 = new User("Virat Kohli", "virat18@example.com", Role.USER);
        user1 = userRepository.save(user1);
        logger.info("🆔 Created user: {}", user1.getUsername());

        User user2 = new User("Anushka Sharma", "anushka@example.com", Role.USER);
        user2 = userRepository.save(user2);
        logger.info("🆔 Created user: {}", user2.getUsername());

        // Create Sample Songs
        Song song1 = new Song("Perfect", "Ed Sheeran", "Deluxe", "Soft Rock", 423);
        song1.setLikeCount(150);
        song1 = songRepository.save(song1);
        logger.info("💿 Created song: {}", song1.getTitle());

        Song song2 = new Song("Baarishein", "Anuv Jain", "Baarishein", "Soft Rock", 391);
        song2.setLikeCount(120);
        song2 = songRepository.save(song2);
        logger.info("💿 Created song: {}", song2.getTitle());

        Song song3 = new Song("Shape of You", "Ed Sheeran", "÷ (Divide)", "Pop", 263);
        song3.setLikeCount(200);
        song3 = songRepository.save(song3);
        logger.info("💿 Created song: {}", song3.getTitle());

        Song song4 = new Song("Arz Kiya Hai", "Anuv Jain", "Coke Studio Bharat", "Indian Pop", 294);
        song4.setLikeCount(180);
        song4 = songRepository.save(song4);
        logger.info("💿 Created song: {}", song4.getTitle());

        Song song5 = new Song("Dawood", "Sidhu Moosewala", "PBX1", "HIP-HOP", 356);
        song5.setLikeCount(95);
        song5 = songRepository.save(song5);
        logger.info("💿 Created song: {}", song5.getTitle());

        Song song6 = new Song("7 years", "Lukas Graham", "7 years", "Pop", 200);
        song6.setLikeCount(250);
        song6 = songRepository.save(song6);
        logger.info("💿 Created song: {}", song6.getTitle());

        // Add some liked songs to users
        user1.likeSong(song1.getId());
        user1.likeSong(song3.getId());
        user1.likeSong(song6.getId());
        userRepository.save(user1);

        user2.likeSong(song2.getId());
        user2.likeSong(song4.getId());
        userRepository.save(user2);

        // Create Sample Playlists
        Playlist playlist1 = new Playlist("Travelling", "90s Bollywood", user1.getId());
        playlist1.addSong(song1.getId());
        playlist1.addSong(song2.getId());
        playlist1.addSong(song5.getId());
        playlist1 = playlistRepository.save(playlist1);
        logger.info("▶️ Created playlist: {} with {} songs", playlist1.getName(), playlist1.getSongCount());

        Playlist playlist2 = new Playlist("Sleeping", "Modern pop favorites", user2.getId());
        playlist2.addSong(song3.getId());
        playlist2.addSong(song4.getId());
        playlist2.addSong(song6.getId());
        playlist2 = playlistRepository.save(playlist2);
        logger.info("▶️ Created playlist: {} with {} songs", playlist2.getName(), playlist2.getSongCount());

        Playlist playlist3 = new Playlist("Workout Mix", "High energy songs for workouts", user1.getId());
        playlist3.addSong(song3.getId());
        playlist3.addSong(song6.getId());
        playlist3 = playlistRepository.save(playlist3);
        logger.info("▶️ Created playlist: {} with {} songs", playlist3.getName(), playlist3.getSongCount());

        logger.info("✅ Sample data loaded successfully!");
        logger.info("📝 Summary:");
        logger.info("   - Users: {} (1 admin, {} regular)", userRepository.count(), userRepository.count() - 1);
        logger.info("   - Songs: {}", songRepository.count());
        logger.info("   - Playlists: {}", playlistRepository.count());

        // Print IDs for testing
        logger.info("🔍 Test IDs for Postman:");
        logger.info("   - Admin ID: {}", admin.getId());
        logger.info("   - User1 ID: {}", user1.getId());
        logger.info("   - User2 ID: {}", user2.getId());
        logger.info("   - Sample Song ID: {}", song1.getId());
        logger.info("   - Sample Playlist ID: {}", playlist1.getId());
    }
}