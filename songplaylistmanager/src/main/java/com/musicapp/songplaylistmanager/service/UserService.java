package com.musicapp.songplaylistmanager.service;


import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Create new user
    public User createUser(User user) {
        logger.info("👤 Creating new user: {}", user.getUsername());

        // Check if username exists
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("❌ Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("❌ Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User savedUser = userRepository.save(user);
        logger.info("✅ User created successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    // Get user by ID
    public Optional<User> getUserById(String id) {
        logger.info("🔍 Searching for user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("✅ User found: {}", user.get().getUsername());
        } else {
            logger.warn("❌ User not found with ID: {}", id);
        }

        return user;
    }

    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        logger.info("🔍 Searching for user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    // Get all users
    public List<User> getAllUsers() {
        logger.info("📋 Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("📊 Found {} users", users.size());
        return users;
    }

    // Update user
    public User updateUser(String id, User updatedUser) {
        logger.info("🔄 Updating user with ID: {}", id);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            logger.error("❌ User not found with ID: {}", id);
            throw new RuntimeException("User not found");
        }

        User user = existingUser.get();

        // Check if new username is taken (if changed)
        if (!user.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            logger.error("❌ Username already exists: {}", updatedUser.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Check if new email is taken (if changed)
        if (!user.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            logger.error("❌ Email already exists: {}", updatedUser.getEmail());
            throw new RuntimeException("Email already exists");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());

        User savedUser = userRepository.save(user);
        logger.info("✅ User updated successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    // Get user's liked songs
    public List<String> getUserLikedSongs(String userId) {
        logger.info("❤️ Fetching liked songs for user ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            logger.error("❌ User not found with ID: {}", userId);
            throw new RuntimeException("User not found");
        }

        List<String> likedSongs = user.get().getLikedSongs();
        logger.info("📊 User has {} liked songs", likedSongs.size());

        return likedSongs;
    }
}