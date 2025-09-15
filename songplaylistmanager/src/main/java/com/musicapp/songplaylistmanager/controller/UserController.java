package com.musicapp.songplaylistmanager.controller;




import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Create user
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            logger.info("👤 API: Creating user: {}", user.getUsername());
            User createdUser = userService.createUser(user);
            logger.info("✅ API: User created successfully with ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("❌ API: Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        logger.info("🔍 API: Fetching user with ID: {}", id);
        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            logger.info("✅ API: User found: {}", user.get().getUsername());
            return ResponseEntity.ok(user.get());
        } else {
            logger.warn("❌ API: User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("📋 API: Fetching all users");
        List<User> users = userService.getAllUsers();
        logger.info("✅ API: Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,
                                        @Valid @RequestBody User user) {
        try {
            logger.info("🔄 API: Updating user with ID: {}", id);
            User updatedUser = userService.updateUser(id, user);
            logger.info("✅ API: User updated successfully: {}", updatedUser.getUsername());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("❌ API: Error updating user: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get user's liked songs
    @GetMapping("/{id}/liked-songs")
    public ResponseEntity<?> getUserLikedSongs(@PathVariable String id) {
        try {
            logger.info("❤️ API: Fetching liked songs for user ID: {}", id);
            List<String> likedSongs = userService.getUserLikedSongs(id);
            logger.info("✅ API: Retrieved {} liked songs", likedSongs.size());
            return ResponseEntity.ok(likedSongs);
        } catch (Exception e) {
            logger.error("❌ API: Error fetching liked songs: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        logger.info("🔍 API: Fetching user by username: {}", username);
        Optional<User> user = userService.getUserByUsername(username);

        if (user.isPresent()) {
            logger.info("✅ API: User found: {}", user.get().getUsername());
            return ResponseEntity.ok(user.get());
        } else {
            logger.warn("❌ API: User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        }
    }
}

