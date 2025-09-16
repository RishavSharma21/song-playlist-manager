package com.musicapp.songplaylistmanager.console;

import com.musicapp.songplaylistmanager.model.Song.Role;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.service.PlaylistService;
import com.musicapp.songplaylistmanager.service.SongService;
import com.musicapp.songplaylistmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleMenuManager {

    @Autowired
    private UserService userService;

    @Autowired
    private SongService songService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserMenuHandler userMenuHandler;

    @Autowired
    private SongMenuHandler songMenuHandler;

    @Autowired
    private PlaylistMenuHandler playlistMenuHandler;

    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public void startApplication() {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔐 LOGIN / REGISTRATION");
        System.out.println("=".repeat(50));
        System.out.println("1. Login");
        System.out.println("2. Register New User");
        System.out.println("3. View All Users (for testing)");
        System.out.println("4. Exit");
        System.out.println("=".repeat(50));
        System.out.print("👤 Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                viewAllUsers();
                break;
            case 4:
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎵 SONG & PLAYLIST MANAGEMENT SYSTEM");
        System.out.println("👤 Logged in as: " + currentUser.getUsername() +
                " (" + currentUser.getRole() + ")");
        System.out.println("=".repeat(60));
        System.out.println("1. User Management");
        System.out.println("2. Song Management" + (currentUser.isAdmin() ? "" : " (View Only)"));
        System.out.println("3. Playlist Management");
        System.out.println("4. Reports & Statistics");
        System.out.println("5. Logout");
        System.out.println("6. Exit");
        System.out.println("=".repeat(60));
        System.out.print("🎯 Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                userMenuHandler.handleUserMenu(currentUser);
                break;
            case 2:
                songMenuHandler.handleSongMenu(currentUser);
                break;
            case 3:
                playlistMenuHandler.handlePlaylistMenu(currentUser);
                break;
            case 4:
                showReports();
                break;
            case 5:
                logout();
                break;
            case 6:
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
    }

    private void login() {
        System.out.println("\n🔐 LOGIN");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        try {
            var userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("✅ Login successful! Welcome, " + currentUser.getUsername());
            } else {
                System.out.println("❌ User not found! Please check username or register first.");
            }
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
    }

    private void register() {
        System.out.println("\n📝 REGISTER NEW USER");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Are you an admin? (y/n): ");
        String adminInput = scanner.nextLine().trim().toLowerCase();
        boolean isAdmin = adminInput.equals("y") || adminInput.equals("yes");

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setRole(isAdmin ? Role.ADMIN : Role.USER);

            User createdUser = userService.createUser(newUser);
            System.out.println("✅ User registered successfully!");
            System.out.println("📋 User Details:");
            System.out.println("   ID: " + createdUser.getId());
            System.out.println("   Username: " + createdUser.getUsername());
            System.out.println("   Role: " + createdUser.getRole());

        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
    }

    private void viewAllUsers() {
        System.out.println("\n👥 ALL USERS");
        try {
            var users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("=".repeat(80));
                System.out.printf("%-25s %-25s %-15s %-15s%n", "ID", "USERNAME", "EMAIL", "ROLE");
                System.out.println("=".repeat(80));
                for (User user : users) {
                    System.out.printf("%-25s %-25s %-15s %-15s%n",
                            user.getId().substring(0, Math.min(24, user.getId().length())),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole());
                }
                System.out.println("=".repeat(80));
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }
    }

    private void showReports() {
        System.out.println("\n📊 REPORTS & STATISTICS");
        try {
            var songs = songService.getAllSongs();
            var users = userService.getAllUsers();
            var playlists = playlistService.getAllPlaylists();

            System.out.println("=".repeat(40));
            System.out.println("📈 SYSTEM STATISTICS");
            System.out.println("=".repeat(40));
            System.out.println("👥 Total Users: " + users.size());
            System.out.println("🎵 Total Songs: " + songs.size());
            System.out.println("📝 Total Playlists: " + playlists.size());

            if (!songs.isEmpty()) {
                var mostLikedSongs = songService.getMostLikedSongs();
                System.out.println("\n🏆 TOP LIKED SONGS:");
                mostLikedSongs.stream().limit(5).forEach(song ->
                        System.out.println("   ❤️ " + song.getTitle() + " by " + song.getArtist() +
                                " (" + song.getLikeCount() + " likes)"));
            }

            System.out.println("=".repeat(40));

        } catch (Exception e) {
            System.out.println("❌ Error generating reports: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void logout() {
        System.out.println("👋 Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    private int getIntInput() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
