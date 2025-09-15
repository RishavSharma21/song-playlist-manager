package com.musicapp.songplaylistmanager.repository;


import com.musicapp.songplaylistmanager.model.Song.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongRepository extends MongoRepository<Song, String> {

    // Find songs by artist
    List<Song> findByArtistContainingIgnoreCase(String artist);

    // Find songs by title
    List<Song> findByTitleContainingIgnoreCase(String title);

    // Find songs by genre
    List<Song> findByGenreIgnoreCase(String genre);

    // Find songs by album
    List<Song> findByAlbumContainingIgnoreCase(String album);

    // Search songs by title or artist
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'artist': { $regex: ?0, $options: 'i' } } ] }")
    List<Song> searchByTitleOrArtist(String searchTerm);

    // Find most liked songs
    List<Song> findTop10ByOrderByLikeCountDesc();
}