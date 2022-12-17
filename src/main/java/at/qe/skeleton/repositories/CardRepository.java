package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import java.util.List;

public interface CardRepository  extends AbstractRepository<Card, Long>{

    Card findFirstById(Long Id);

    List<Card> findByContainer(Deck container);

}
