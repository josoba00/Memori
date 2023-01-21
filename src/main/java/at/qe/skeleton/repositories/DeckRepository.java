package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeckRepository extends AbstractRepository<Deck, Long>{

    List<Deck> findAllByCreator(User creator);

    @Query("Select u From Deck u WHERE u.status = 'PUBLIC'")
    List<Deck> findAllByStatusIsPublic();

    Deck findById(Long id);

    List<Deck> findAllByTitleContaining(String title);

    @Query("Select u From Deck u WHERE u.status = 'PUBLIC' AND UPPER(u.title) LIKE UPPER(concat('%', ?1,'%')) AND NOT u.creator.username = ?2")
    List<Deck> findBySearch(String search, String username);
    @Query("Select u From Deck u WHERE (u.status = 'PUBLIC' OR u.status = 'LOCKED') AND UPPER(u.title) LIKE UPPER(concat('%', ?1,'%'))")
    List<Deck> findByAdminSearch(String search);
    // Tried with Modifying Annotation and own Query but still didn't work or threw other exception
    @Modifying
    @Query("Delete From Deck u WHERE u.id = :deckID")
    void deleteById(@Param("deckID") Long id);

}
