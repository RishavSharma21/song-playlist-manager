package com.musicapp.songplaylistmanager.console;


import com.musicapp.songplaylistmanager.model.Song.Role;
import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.service.UserService;
import com.musicapp.songplaylistmanager.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class UserMenuHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private SongService songService; // New field for SongService

    private Scanner scanner = new Scanner(System.in);

    public void handleUserMenu(User currentUser) {
        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("👤 USER MANAGEMENT");
            System.out.println("=".repeat(50));

            if (currentUser.isAdmin()) {
                System.out.println("1. View All Users");
                System.out.println("2. Search User by Username");
                System.out.println("3. Update User Role");
                System.out.println("4. View User Statistics");
                System.out.println("5. My Profile");
                System.out.println("6. Update My Profile");
                System.out.println("7. View My Liked Songs");
                System.out.println("8. Back to Main Menu");
            } else {
                System.out.println("1. My Profile");
                System.out.println("2. Update My Profile");
                System.out.println("3. View My Liked Songs");
                System.out.println("4. View All Users");
                System.out.println("5. Back to Main Menu");
            }

            System.out.println("=".repeat(50));
            System.out.print("🎯 Choose an option: ");

            int choice = getIntInput();

            if (currentUser.isAdmin()) {
                keepRunning = handleAdminUserMenu(choice, currentUser);
            } else {
                keepRunning = handleRegularUserMenu(choice, currentUser);
            }
        }
    }

    private boolean handleAdminUserMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                viewAllUsers();
                break;
            case 2:
                searchUserByUsername();
                break;
            case 3:
                updateUserRole();
                break;
            case 4:
                viewUserStatistics();
                break;
            case 5:
                viewMyProfile(currentUser);
                break;
            case 6:
                updateMyProfile(currentUser);
                break;
            case 7:
                viewMyLikedSongs(currentUser);
                break;
            case 8:
                return false;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private boolean handleRegularUserMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                viewMyProfile(currentUser);
                break;
            case 2:
                updateMyProfile(currentUser);
                break;
            case 3:
                viewMyLikedSongs(currentUser);
                break;
            case 4:
                viewAllUsers();
                break;
            case 5:
                return false;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private void viewAllUsers() {
        System.out.println("\n👥 ALL USERS");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }

            System.out.println("=".repeat(100));
            System.out.printf("%-25s %-20s %-30s %-15s %-10s%n",
                    "ID", "USERNAME", "EMAIL", "ROLE", "LIKED SONGS");
            System.out.println("=".repeat(100));

            for (User user : users) {
                System.out.printf("%-25s %-20s %-30s %-15s %-10s%n",
                        user.getId().substring(0, Math.min(24, user.getId().length())),
                        truncate(user.getUsername(), 19),
                        truncate(user.getEmail(), 29),
                        user.getRole(),
                        user.getLikedSongs().size());
            }
            System.out.println("=".repeat(100));
            System.out.println("Total users: " + users.size());

        } catch (Exception e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchUserByUsername() {
        System.out.println("\n🔍 SEARCH USER");
        System.out.print("Enter username to search: ");
        String username = scanner.nextLine().trim();

        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("✅ User found:");
                printUserDetails(user);
            } else {
                System.out.println("❌ User not found: " + username);
            }

        } catch (Exception e) {
            System.out.println("❌ Search failed: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateUserRole() {
        System.out.println("\n🔄 UPDATE USER ROLE");
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine().trim();

        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isEmpty()) {
                System.out.println("❌ User not found: " + username);
                return;
            }

            User user = userOpt.get();
            System.out.println("Current user: " + user.getUsername() + " (Role: " + user.getRole() + ")");

            System.out.println("Select new role:");
            System.out.println("1. USER");
            System.out.println("2. ADMIN");
            System.out.print("Choose role (1 or 2): ");

            int roleChoice = getIntInput();
            scanner.nextLine();

            Role newRole;
            if (roleChoice == 1) {
                newRole = Role.USER;
            } else if (roleChoice == 2) {
                newRole = Role.ADMIN;
            } else {
                System.out.println("❌ Invalid role selection!");
                return;
            }

            user.setRole(newRole);
            User updatedUser = userService.updateUser(user.getId(), user);

            System.out.println("✅ User role updated successfully!");
            System.out.println("User: " + updatedUser.getUsername() + " is now " + updatedUser.getRole());

        } catch (Exception e) {
            System.out.println("❌ Failed to update user role: " + e.getMessage());
        }
    }

    private void viewUserStatistics() {
        System.out.println("\n📊 USER STATISTICS");
        try {
            List<User> users = userService.getAllUsers();

            long adminCount = users.stream().filter(User::isAdmin).count();
            long userCount = users.size() - adminCount;

            System.out.println("=".repeat(40));
            System.out.println("📈 USER STATISTICS");
            System.out.println("=".repeat(40));
            System.out.println("👑 Total Admins: " + adminCount);
            System.out.println("👤 Total Regular Users: " + userCount);
            System.out.println("📊 Total Users: " + users.size());

            System.out.println("\n🏆 MOST ACTIVE USERS (by liked songs):");
            users.stream()
                    .sorted((u1, u2) -> Integer.compare(u2.getLikedSongs().size(), u1.getLikedSongs().size()))
                    .limit(5)
                    .forEach(user ->
                            System.out.println("   👤 " + user.getUsername() + " - " +
                                    user.getLikedSongs().size() + " liked songs"));

            System.out.println("=".repeat(40));

        } catch (Exception e) {
            System.out.println("❌ Error generating statistics: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewMyProfile(User currentUser) {
        System.out.println("\n👤 MY PROFILE");
        try {
            Optional<User> userOpt = userService.getUserById(currentUser.getId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                printUserDetails(user);
            } else {
                System.out.println("❌ Could not load profile data.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading profile: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateMyProfile(User currentUser) {
        System.out.println("\n🔄 UPDATE MY PROFILE");
        System.out.println("Current details:");
        printUserDetails(currentUser);

        System.out.print("Enter new username (press Enter to keep current): ");
        String newUsername = scanner.nextLine().trim();

        System.out.print("Enter new email (press Enter to keep current): ");
        String newEmail = scanner.nextLine().trim();

        try {
            User userToUpdate = new User();
            userToUpdate.setUsername(newUsername.isEmpty() ? currentUser.getUsername() : newUsername);
            userToUpdate.setEmail(newEmail.isEmpty() ? currentUser.getEmail() : newEmail);
            userToUpdate.setRole(currentUser.getRole());

            User updatedUser = userService.updateUser(currentUser.getId(), userToUpdate);

            System.out.println("✅ Profile updated successfully!");
            printUserDetails(updatedUser);

            currentUser.setUsername(updatedUser.getUsername());
            currentUser.setEmail(updatedUser.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Failed to update profile: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewMyLikedSongs(User currentUser) {
        System.out.println("\n❤️ MY LIKED SONGS");
        try {
            List<String> likedSongIds = userService.getUserLikedSongs(currentUser.getId());
            if (likedSongIds.isEmpty()) {
                System.out.println("You haven't liked any songs yet.");
                return;
            }

            System.out.println("=".repeat(80));
            System.out.println("❤️ YOUR LIKED SONGS (" + likedSongIds.size() + " songs)");
            System.out.println("=".repeat(80));

            for (String songId : likedSongIds) {
                Optional<Song> songOpt = songService.getSongById(songId);
                if (songOpt.isPresent()) {
                    Song song = songOpt.get();
                    System.out.println("🎵 " + song.getTitle() + " by " + song.getArtist() + " (ID: " + song.getId() + ")");
                } else {
                    System.out.println("❌ Song with ID: " + songId + " not found.");
                }
            }

            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.out.println("❌ Error fetching liked songs: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void printUserDetails(User user) {
        System.out.println("=".repeat(50));
        System.out.println("📋 USER DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("🆔 ID: " + user.getId());
        System.out.println("👤 Username: " + user.getUsername());
        System.out.println("📧 Email: " + user.getEmail());
        System.out.println("🔖 Role: " + user.getRole());
        System.out.println("❤️ Liked Songs: " + user.getLikedSongs().size());
        System.out.println("📅 Created: " + user.getCreatedDate());
        System.out.println("=".repeat(50));
    }

    private int getIntInput() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String truncate(String str, int length) {
        if (str == null) return "N/A";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}