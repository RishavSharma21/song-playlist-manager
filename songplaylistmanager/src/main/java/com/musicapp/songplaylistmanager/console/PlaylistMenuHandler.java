package com.musicapp.songplaylistmanager.console;

import com.musicapp.songplaylistmanager.model.Song.Playlist;
import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.service.PlaylistService;
import com.musicapp.songplaylistmanager.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class PlaylistMenuHandler {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SongService songService;

    private Scanner scanner = new Scanner(System.in);

    public void handlePlaylistMenu(User currentUser) {
        boolean running = true;
        while (running) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("📝 PLAYLIST MANAGEMENT");
            System.out.println("=".repeat(50));

            if (currentUser.isAdmin()) {
                System.out.println("1. View All Playlists (Admin)");
                System.out.println("2. My Playlists");
                System.out.println("3. Create New Playlist");
                System.out.println("4. Update Playlist");
                System.out.println("5. Delete Playlist");
                System.out.println("6. Add Song to Playlist");
                System.out.println("7. Remove Song from Playlist");
                System.out.println("8. View Playlist Details");
                System.out.println("9. Back to Main Menu");
            } else {
                System.out.println("1. My Playlists");
                System.out.println("2. Create New Playlist");
                System.out.println("3. Update My Playlist");
                System.out.println("4. Delete My Playlist");
                System.out.println("5. Add Song to My Playlist");
                System.out.println("6. Remove Song from My Playlist");
                System.out.println("7. View Playlist Details");
                System.out.println("8. Back to Main Menu");
            }

            System.out.println("=".repeat(50));
            System.out.print("🎯 Choose an option: ");

            int choice = getIntInput();

            if (currentUser.isAdmin()) {
                running = handleAdminPlaylistMenu(choice, currentUser);
            } else {
                running = handleUserPlaylistMenu(choice, currentUser);
            }
        }
    }

    private boolean handleAdminPlaylistMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                viewAllPlaylists();
                break;
            case 2:
                viewMyPlaylists(currentUser);
                break;
            case 3:
                createPlaylist(currentUser);
                break;
            case 4:
                updatePlaylist(currentUser);
                break;
            case 5:
                deletePlaylist(currentUser);
                break;
            case 6:
                addSongToPlaylist(currentUser);
                break;
            case 7:
                removeSongFromPlaylist(currentUser);
                break;
            case 8:
                viewPlaylistDetails();
                break;
            case 9:
                return false;  // exit menu
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private boolean handleUserPlaylistMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                viewMyPlaylists(currentUser);
                break;
            case 2:
                createPlaylist(currentUser);
                break;
            case 3:
                updatePlaylist(currentUser);
                break;
            case 4:
                deletePlaylist(currentUser);
                break;
            case 5:
                addSongToPlaylist(currentUser);
                break;
            case 6:
                removeSongFromPlaylist(currentUser);
                break;
            case 7:
                viewPlaylistDetails();
                break;
            case 8:
                return false;  // exit menu
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private void viewAllPlaylists() {
        System.out.println("\n📝 ALL PLAYLISTS (ADMIN VIEW)");
        try {
            List<Playlist> playlists = playlistService.getAllPlaylists();
            if (playlists.isEmpty()) {
                System.out.println("No playlists found.");
                return;
            }

            System.out.println("=".repeat(120));
            System.out.printf("%-25s %-20s %-30s %-25s %-10s%n",
                    "ID", "NAME", "DESCRIPTION", "USER_ID", "SONGS");
            System.out.println("=".repeat(120));

            for (Playlist playlist : playlists) {
                System.out.printf("%-25s %-20s %-30s %-25s %-10s%n",
                        playlist.getId().substring(0, Math.min(24, playlist.getId().length())),
                        truncate(playlist.getName(), 19),
                        truncate(playlist.getDescription() != null ? playlist.getDescription() : "No description", 29),
                        playlist.getUserId().substring(0, Math.min(24, playlist.getUserId().length())),
                        playlist.getSongCount());
            }
            System.out.println("=".repeat(120));
            System.out.println("Total playlists: " + playlists.size());

        } catch (Exception e) {
            System.out.println("❌ Error fetching playlists: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewMyPlaylists(User currentUser) {
        System.out.println("\n📝 MY PLAYLISTS");
        try {
            List<Playlist> playlists = playlistService.getUserPlaylists(currentUser.getId());
            if (playlists.isEmpty()) {
                System.out.println("You don't have any playlists yet. Create your first playlist!");
                return;
            }

            System.out.println("=".repeat(100));
            System.out.printf("%-25s %-25s %-40s %-10s%n",
                    "ID", "NAME", "DESCRIPTION", "SONGS");
            System.out.println("=".repeat(100));

            for (Playlist playlist : playlists) {
                System.out.printf("%-25s %-25s %-40s %-10s%n",
                        playlist.getId().substring(0, Math.min(24, playlist.getId().length())),
                        truncate(playlist.getName(), 24),
                        truncate(playlist.getDescription() != null ? playlist.getDescription() : "No description", 39),
                        playlist.getSongCount());
            }
            System.out.println("=".repeat(100));
            System.out.println("Your playlists: " + playlists.size());

        } catch (Exception e) {
            System.out.println("❌ Error fetching your playlists: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void createPlaylist(User currentUser) {
        System.out.println("\n➕ CREATE NEW PLAYLIST");

        System.out.print("Enter playlist name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Playlist name cannot be empty!");
            return;
        }

        System.out.print("Enter playlist description (optional): ");
        String description = scanner.nextLine().trim();

        try {
            Playlist newPlaylist = new Playlist(name, description.isEmpty() ? null : description, currentUser.getId());
            Playlist createdPlaylist = playlistService.createPlaylist(newPlaylist);

            System.out.println("✅ Playlist created successfully!");
            System.out.println("📋 Playlist Details:");
            System.out.println("   ID: " + createdPlaylist.getId());
            System.out.println("   Name: " + createdPlaylist.getName());
            System.out.println("   Description: " + (createdPlaylist.getDescription() != null ?
                    createdPlaylist.getDescription() : "No description"));
            System.out.println("   Songs: 0 (empty playlist)");

        } catch (Exception e) {
            System.out.println("❌ Failed to create playlist: " + e.getMessage());
        }
    }

    private void updatePlaylist(User currentUser) {
        System.out.println("\n🔄 UPDATE PLAYLIST");
        viewMyPlaylists(currentUser);

        System.out.print("Enter Playlist ID to update: ");
        String playlistId = scanner.nextLine().trim();

        try {
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (playlistOpt.isEmpty()) {
                System.out.println("❌ Playlist not found!");
                return;
            }

            Playlist playlist = playlistOpt.get();

            // Authorization check
            if (!currentUser.isAdmin() && !playlist.getUserId().equals(currentUser.getId())) {
                System.out.println("❌ You do not have permission to update this playlist.");
                return;
            }

            System.out.println("Current playlist: " + playlist.getName());

            System.out.print("Enter new name (press Enter to keep current): ");
            String newName = scanner.nextLine().trim();

            System.out.print("Enter new description (press Enter to keep current): ");
            String newDescription = scanner.nextLine().trim();

            Playlist updatePlaylist = new Playlist();
            updatePlaylist.setName(newName.isEmpty() ? playlist.getName() : newName);
            updatePlaylist.setDescription(newDescription.isEmpty() ? playlist.getDescription() : newDescription);

            Playlist updatedPlaylist = playlistService.updatePlaylist(playlistId, updatePlaylist, currentUser.getId());

            System.out.println("✅ Playlist updated successfully!");
            System.out.println("Updated: " + updatedPlaylist.getName());
            if (updatedPlaylist.getDescription() != null) {
                System.out.println("Description: " + updatedPlaylist.getDescription());
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to update playlist: " + e.getMessage());
        }
    }

    private void deletePlaylist(User currentUser) {
        System.out.println("\n❌ DELETE PLAYLIST");
        viewMyPlaylists(currentUser);

        System.out.print("Enter Playlist ID to delete: ");
        String playlistId = scanner.nextLine().trim();

        try {
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (playlistOpt.isEmpty()) {
                System.out.println("❌ Playlist not found!");
                return;
            }

            Playlist playlist = playlistOpt.get();

            // Authorization check
            if (!currentUser.isAdmin() && !playlist.getUserId().equals(currentUser.getId())) {
                System.out.println("❌ You do not have permission to delete this playlist.");
                return;
            }

            System.out.print("Are you sure you want to delete the playlist '" + playlist.getName() + "'? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            playlistService.deletePlaylist(playlistId, currentUser.getId());
            System.out.println("✅ Playlist deleted successfully!");

        } catch (Exception e) {
            System.out.println("❌ Failed to delete playlist: " + e.getMessage());
        }
    }

    private void addSongToPlaylist(User currentUser) {
        System.out.println("\n➕ ADD SONG TO PLAYLIST");
        viewMyPlaylists(currentUser);

        System.out.print("Enter Playlist ID: ");
        String playlistId = scanner.nextLine().trim();

        try {
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (playlistOpt.isEmpty()) {
                System.out.println("❌ Playlist not found!");
                return;
            }

            Playlist playlist = playlistOpt.get();

            if (!currentUser.isAdmin() && !playlist.getUserId().equals(currentUser.getId())) {
                System.out.println("❌ You do not have permission to modify this playlist.");
                return;
            }

            System.out.print("Enter Song ID to add: ");
            String songId = scanner.nextLine().trim();

            Optional<Song> songOpt = songService.getSongById(songId);
            if (songOpt.isEmpty()) {
                System.out.println("❌ Song not found!");
                return;
            }

            playlistService.addSongToPlaylist(playlistId, songId, currentUser.getId());
            System.out.println("✅ Song added to playlist successfully!");

        } catch (Exception e) {
            System.out.println("❌ Failed to add song: " + e.getMessage());
        }
    }

    private void removeSongFromPlaylist(User currentUser) {
        System.out.println("\n➖ REMOVE SONG FROM PLAYLIST");
        viewMyPlaylists(currentUser);

        System.out.print("Enter Playlist ID: ");
        String playlistId = scanner.nextLine().trim();

        try {
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (playlistOpt.isEmpty()) {
                System.out.println("❌ Playlist not found!");
                return;
            }

            Playlist playlist = playlistOpt.get();

            if (!currentUser.isAdmin() && !playlist.getUserId().equals(currentUser.getId())) {
                System.out.println("❌ You do not have permission to modify this playlist.");
                return;
            }

            List<String> songIds = playlist.getSongIds();
            if (songIds == null || songIds.isEmpty()) {
                System.out.println("This playlist has no songs to remove.");
                return;
            }

            System.out.println("Songs in playlist:");
            for (int i = 0; i < songIds.size(); i++) {
                String songId = songIds.get(i);
                Optional<Song> songOpt = songService.getSongById(songId);
                String songInfo = songOpt.map(song -> song.getTitle() + " by " + song.getArtist())
                        .orElse("Unknown Song");
                System.out.printf("%d. %s (ID: %s)%n", i + 1, songInfo, songId);
            }

            System.out.print("Enter the number of the song to remove: ");
            int songIndex = getIntInput();

            if (songIndex < 1 || songIndex > songIds.size()) {
                System.out.println("❌ Invalid song number.");
                return;
            }

            String songIdToRemove = songIds.get(songIndex - 1);

            playlistService.removeSongFromPlaylist(playlistId, songIdToRemove, currentUser.getId());
            System.out.println("✅ Song removed from playlist successfully!");

        } catch (Exception e) {
            System.out.println("❌ Failed to remove song: " + e.getMessage());
        }
    }

    private void viewPlaylistDetails() {
        System.out.println("\n📋 VIEW PLAYLIST DETAILS");

        System.out.print("Enter Playlist ID: ");
        String playlistId = scanner.nextLine().trim();

        try {
            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (playlistOpt.isEmpty()) {
                System.out.println("❌ Playlist not found!");
                return;
            }

            Playlist playlist = playlistOpt.get();
            System.out.println("\nPlaylist: " + playlist.getName());
            System.out.println("Description: " + (playlist.getDescription() != null ? playlist.getDescription() : "No description"));
            System.out.println("Songs:");

            List<String> songIds = playlist.getSongIds();
            if (songIds == null || songIds.isEmpty()) {
                System.out.println("  No songs in this playlist.");
            } else {
                for (int i = 0; i < songIds.size(); i++) {
                    String songId = songIds.get(i);
                    Optional<Song> songOpt = songService.getSongById(songId);
                    String songInfo = songOpt.map(song -> song.getTitle() + " by " + song.getArtist())
                            .orElse("Unknown Song");
                    System.out.printf("  %d. %s (ID: %s)%n", i + 1, songInfo, songId);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to retrieve playlist details: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private int getIntInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number, please enter a valid integer: ");
            }
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}
