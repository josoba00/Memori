package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DeckRepository extends AbstractRepository<Deck, Long>{

    List<Deck> findAllByCreator(User creator);

    @Query("Select u From Deck u WHERE u.status = 'PUBLIC'")
    List<Deck> findAllByStatusIsPublic();

    List<Deck> findAllByTitleContaining(String title);
}
