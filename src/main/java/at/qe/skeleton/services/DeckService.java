package at.qe.skeleton.services;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Scope("application")
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<Deck> getAllDecks() {return deckRepository.findAll();}

    public Deck saveDeck(Deck deck) {
        if (deck.isNew()) {
            deck.setCreationDate(new Date());
        }
        return deckRepository.save(deck);
    }

    public void deleteDeck(Deck deck){
        deckRepository.delete(deck);
    }

    public Deck loadDeck(Long id){
        return deckRepository.getReferenceById(id);
    }

    public List<Deck> loadAllForeignPublicDecks(User currentUser){
        List<Deck> allDecks =  deckRepository.findAllByStatusIsPublic();
        allDecks.removeIf(loadOwnDecks(currentUser)::contains);
        return allDecks;
    }

    public List<Deck> loadDecksWithTitle(String title){
        return deckRepository.findAllByTitleContaining(title);
    }

    public List<Deck> loadOwnDecks(User currentUser){
        return deckRepository.findAllByCreator(currentUser);
    }

    public void addCardToDeck(Card card, Deck deck) throws InstanceAlreadyExistsException {
        if (deck.getContent().stream().anyMatch(u -> u.equals(card))) {
            throw new InstanceAlreadyExistsException();
        }
        Set<Card> temp = deck.getContent();
        temp.add(card);
        deck.setContent(temp);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void lockDeck(Deck deck){
        deck.setStatus(DeckStatus.LOCKED);
    }

    /**
     * Unlocks Deck and sets it implicitly to Private because no record of previous status.
     * @param deck
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void unlockDeck(Deck deck){
        deck.setStatus(DeckStatus.PRIVATE);
    }

    public void setDeckStatusPublic(Deck deck){
        deck.setStatus(DeckStatus.PUBLIC);
    }

    public void setDeckStatusPrivate(Deck deck){
        deck.setStatus(DeckStatus.PRIVATE);
    }

}
