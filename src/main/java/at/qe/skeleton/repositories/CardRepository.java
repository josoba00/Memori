package at.qe.skeleton.repositories;


import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.CardInterface;

import java.util.List;

/**
 * Repository for managing {@link Card} entitties.
 */
public interface CardRepository extends AbstractRepository<Card, Long> {
    Card findFirstByCardId(long cardId);

    List<Card> findByDeckId(long deckId);

}
