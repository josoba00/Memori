package at.qe.skeleton.services;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.ui.beans.SessionInfoBean;
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

    @Autowired
    private UserService userService;
    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private MessageSenderService messageSenderService;

    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<Deck> getAllDecks() {return deckRepository.findAll();}

    public Deck saveDeck(Deck deck) {
        if (deck.isNew()) {
            deck.setCreationDate(new Date());
        }
        return deckRepository.save(deck);
    }

    public void deleteDeck(Deck deck, User currentUser){
        if (currentUser.getBookmarks().contains(deck)){
            userService.deleteBookmark(currentUser, deck);
        }
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

    public List<Deck> loadDecksBySearch(String search){
        return deckRepository.findBySearch(search, sessionInfoBean.getCurrentUserName());
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
    public void lockDeck(Deck deck) {
        if (deck.getStatus() == DeckStatus.PRIVATE) {
            throw new IllegalStateException("Private Decks can't be locked");
        }
        deck.setStatus(DeckStatus.LOCKED);
        messageSenderService.sendDeckLockMessage(deck);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void unlockDeck(Deck deck) {
        if (deck.getStatus() != DeckStatus.LOCKED) {
            throw new IllegalStateException("Deck was not locked");
        }
        deck.setStatus(DeckStatus.PUBLIC);
    }

    public void setDeckStatusPublic(Deck deck){
        if(deck.getStatus() != DeckStatus.PRIVATE)
        {
            throw new IllegalStateException("Tried to set deck to public but was not private");
        }
        deck.setStatus(DeckStatus.PUBLIC);
    }

    public void setDeckStatusPrivate(Deck deck){
        if(deck.getStatus() != DeckStatus.PUBLIC){
            throw new IllegalStateException("Tried to set deck to private but was not public");
        }
        deck.setStatus(DeckStatus.PRIVATE);
    }

}
