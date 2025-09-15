package com.musicapp.songplaylistmanager.repository;


import com.musicapp.songplaylistmanager.model.Song.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {

    // Find playlists by user ID
    List<Playlist> findByUserId(String userId);

    // Find playlists by name
    List<Playlist> findByNameContainingIgnoreCase(String name);

    // Find playlists containing a specific song
    List<Playlist> findBySongIdsContaining(String songId);

    // Count playlists by user
    long countByUserId(String userId);
}