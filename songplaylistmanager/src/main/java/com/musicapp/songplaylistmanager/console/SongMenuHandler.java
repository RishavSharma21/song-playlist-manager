package com.musicapp.songplaylistmanager.console;

import com.musicapp.songplaylistmanager.model.Song.Song;
import com.musicapp.songplaylistmanager.model.Song.User;
import com.musicapp.songplaylistmanager.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class SongMenuHandler {

    @Autowired
    private SongService songService;

    private Scanner scanner = new Scanner(System.in);

    public void handleSongMenu(User currentUser) {
        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎵 SONG MANAGEMENT");
            System.out.println("=".repeat(50));

            if (currentUser.isAdmin()) {
                System.out.println("1. Add New Song");
                System.out.println("2. Update Song");
                System.out.println("3. Delete Song");
                System.out.println("4. View All Songs");
                System.out.println("5. Search Songs");
                System.out.println("6. View Popular Songs");
                System.out.println("7. Like/Unlike Song");
                System.out.println("8. Back to Main Menu");
            } else {
                System.out.println("1. View All Songs");
                System.out.println("2. Search Songs");
                System.out.println("3. View Popular Songs");
                System.out.println("4. Like/Unlike Song");
                System.out.println("5. View Songs by Genre");
                System.out.println("6. Back to Main Menu");
            }

            System.out.println("=".repeat(50));
            System.out.print("🎯 Choose an option: ");

            int choice = getIntInput();

            if (currentUser.isAdmin()) {
                keepRunning = handleAdminSongMenu(choice, currentUser);
            } else {
                keepRunning = handleUserSongMenu(choice, currentUser);
            }
        }
    }

    private boolean handleAdminSongMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                addNewSong(currentUser);
                break;
            case 2:
                updateSong(currentUser);
                break;
            case 3:
                deleteSong(currentUser);
                break;
            case 4:
                viewAllSongs();
                break;
            case 5:
                searchSongs();
                break;
            case 6:
                viewPopularSongs();
                break;
            case 7:
                likeSong(currentUser);
                break;
            case 8:
                return false; // go back to main menu
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private boolean handleUserSongMenu(int choice, User currentUser) {
        switch (choice) {
            case 1:
                viewAllSongs();
                break;
            case 2:
                searchSongs();
                break;
            case 3:
                viewPopularSongs();
                break;
            case 4:
                likeSong(currentUser);
                break;
            case 5:
                viewSongsByGenre();
                break;
            case 6:
                return false; // go back to main menu
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        return true;
    }

    private void addNewSong(User currentUser) {
        System.out.println("\n➕ ADD NEW SONG");

        System.out.print("Enter song title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter artist name: ");
        String artist = scanner.nextLine().trim();

        System.out.print("Enter album name: ");
        String album = scanner.nextLine().trim();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        System.out.print("Enter duration (in seconds): ");
        int duration = getIntInput();
        if (duration <= 0) {
            System.out.println("❌ Invalid duration!");
            return;
        }
        scanner.nextLine(); // consume newline

        try {
            Song newSong = new Song(title, artist, album, genre, duration);
            Song createdSong = songService.createSong(newSong, currentUser.getId());

            System.out.println("✅ Song added successfully!");
            System.out.println("📋 Song Details:");
            System.out.println("   ID: " + createdSong.getId());
            System.out.println("   Title: " + createdSong.getTitle());
            System.out.println("   Artist: " + createdSong.getArtist());
            System.out.println("   Album: " + createdSong.getAlbum());
            System.out.println("   Genre: " + createdSong.getGenre());
            System.out.println("   Duration: " + createdSong.getDuration() + " seconds");

        } catch (Exception e) {
            System.out.println("❌ Failed to add song: " + e.getMessage());
        }
    }

    private void viewAllSongs() {
        System.out.println("\n🎵 ALL SONGS");
        try {
            List<Song> songs = songService.getAllSongs();
            if (songs.isEmpty()) {
                System.out.println("No songs found.");
                return;
            }

            System.out.println("=".repeat(120));
            System.out.printf("%-25s %-20s %-20s %-15s %-15s %-10s%n",
                    "ID", "TITLE", "ARTIST", "ALBUM", "GENRE", "LIKES");
            System.out.println("=".repeat(120));

            for (Song song : songs) {
                System.out.printf("%-25s %-20s %-20s %-15s %-15s %-10s%n",
                        song.getId().substring(0, Math.min(24, song.getId().length())),
                        truncate(song.getTitle(), 19),
                        truncate(song.getArtist(), 19),
                        truncate(song.getAlbum() != null ? song.getAlbum() : "N/A", 14),
                        truncate(song.getGenre() != null ? song.getGenre() : "N/A", 14),
                        song.getLikeCount());
            }
            System.out.println("=".repeat(120));
            System.out.println("Total songs: " + songs.size());

        } catch (Exception e) {
            System.out.println("❌ Error fetching songs: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateSong(User currentUser) {
        System.out.println("\n🔄 UPDATE SONG");
        viewAllSongs();

        System.out.print("Enter Song ID to update: ");
        String songId = scanner.nextLine().trim();

        try {
            Optional<Song> songOpt = songService.getSongById(songId);
            if (songOpt.isEmpty()) {
                System.out.println("❌ Song not found!");
                return;
            }

            Song song = songOpt.get();
            System.out.println("Current song details: " + song.getTitle() + " by " + song.getArtist());

            System.out.print("Enter new title (press Enter to keep current): ");
            String newTitle = scanner.nextLine().trim();
            if (!newTitle.isEmpty()) {
                song.setTitle(newTitle);
            }

            System.out.print("Enter new artist (press Enter to keep current): ");
            String newArtist = scanner.nextLine().trim();
            if (!newArtist.isEmpty()) {
                song.setArtist(newArtist);
            }

            System.out.print("Enter new album (press Enter to keep current): ");
            String newAlbum = scanner.nextLine().trim();
            if (!newAlbum.isEmpty()) {
                song.setAlbum(newAlbum);
            }

            System.out.print("Enter new genre (press Enter to keep current): ");
            String newGenre = scanner.nextLine().trim();
            if (!newGenre.isEmpty()) {
                song.setGenre(newGenre);
            }

            Song updatedSong = songService.updateSong(songId, song, currentUser.getId());
            System.out.println("✅ Song updated successfully!");
            System.out.println("Updated: " + updatedSong.getTitle() + " by " + updatedSong.getArtist());

        } catch (Exception e) {
            System.out.println("❌ Failed to update song: " + e.getMessage());
        }
    }

    private void deleteSong(User currentUser) {
        System.out.println("\n🗑️ DELETE SONG");
        viewAllSongs();

        System.out.print("Enter Song ID to delete: ");
        String songId = scanner.nextLine().trim();

        try {
            Optional<Song> songOpt = songService.getSongById(songId);
            if (songOpt.isEmpty()) {
                System.out.println("❌ Song not found!");
                return;
            }

            Song song = songOpt.get();
            System.out.println("Are you sure you want to delete: " + song.getTitle() + " by " + song.getArtist() + "?");
            System.out.print("Type 'YES' to confirm: ");
            String confirmation = scanner.nextLine().trim();

            if ("YES".equals(confirmation)) {
                songService.deleteSong(songId, currentUser.getId());
                System.out.println("✅ Song deleted successfully!");
            } else {
                System.out.println("❌ Deletion cancelled.");
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to delete song: " + e.getMessage());
        }
    }

    private void searchSongs() {
        System.out.println("\n🔍 SEARCH SONGS");
        System.out.print("Enter search term (title or artist): ");
        String searchTerm = scanner.nextLine().trim();

        try {
            List<Song> songs = songService.searchSongs(searchTerm);
            if (songs.isEmpty()) {
                System.out.println("No songs found matching: " + searchTerm);
                return;
            }

            System.out.println("🔍 Search Results for: " + searchTerm);
            System.out.println("=".repeat(80));
            for (Song song : songs) {
                System.out.println("🎵 " + song.getTitle() + " by " + song.getArtist() +
                        " (" + song.getLikeCount() + " likes)");
                System.out.println("   Album: " + (song.getAlbum() != null ? song.getAlbum() : "N/A") +
                        " | Genre: " + (song.getGenre() != null ? song.getGenre() : "N/A"));
                System.out.println("   ID: " + song.getId());
                System.out.println();
            }
            System.out.println("=".repeat(80));
            System.out.println("Total results: " + songs.size());

        } catch (Exception e) {
            System.out.println("❌ Search failed: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewPopularSongs() {
        System.out.println("\n🏆 POPULAR SONGS");
        try {
            List<Song> popularSongs = songService.getMostLikedSongs();
            if (popularSongs.isEmpty()) {
                System.out.println("No songs found.");
                return;
            }

            System.out.println("=".repeat(80));
            System.out.println("🏆 TOP LIKED SONGS");
            System.out.println("=".repeat(80));

            for (int i = 0; i < popularSongs.size(); i++) {
                Song song = popularSongs.get(i);
                System.out.println((i + 1) + ". 🎵 " + song.getTitle() + " by " + song.getArtist());
                System.out.println("   ❤️ " + song.getLikeCount() + " likes | " +
                        (song.getGenre() != null ? song.getGenre() : "Unknown genre"));
                System.out.println("   ID: " + song.getId());
                System.out.println();
            }
            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.out.println("❌ Error fetching popular songs: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void likeSong(User currentUser) {
        System.out.println("\n❤️ LIKE/UNLIKE SONG");
        System.out.print("Enter Song ID: ");
        String songId = scanner.nextLine().trim();

        try {
            Song song = songService.toggleLikeSong(songId, currentUser.getId());
            System.out.println("✅ Like status updated!");
            System.out.println("🎵 " + song.getTitle() + " by " + song.getArtist() +
                    " now has " + song.getLikeCount() + " likes");

        } catch (Exception e) {
            System.out.println("❌ Failed to update like status: " + e.getMessage());
        }
    }

    private void viewSongsByGenre() {
        System.out.println("\n🎼 SONGS BY GENRE");
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        try {
            List<Song> songs = songService.getSongsByGenre(genre);
            if (songs.isEmpty()) {
                System.out.println("No songs found in genre: " + genre);
                return;
            }

            System.out.println("🎼 Songs in genre: " + genre);
            System.out.println("=".repeat(80));
            for (Song song : songs) {
                System.out.println("🎵 " + song.getTitle() + " by " + song.getArtist() +
                        " (" + song.getLikeCount() + " likes)");
            }
            System.out.println("=".repeat(80));
            System.out.println("Total songs in " + genre + ": " + songs.size());

        } catch (Exception e) {
            System.out.println("❌ Error fetching songs by genre: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
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
