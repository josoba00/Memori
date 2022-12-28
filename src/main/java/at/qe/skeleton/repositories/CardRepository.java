package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import java.util.List;

public interface CardRepository  extends AbstractRepository<Card, Long>{

    List<Card> findAllByContainerId(Long containerId);

    List<Card> findAllByContainer(Deck container);

}
