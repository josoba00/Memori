package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("view")
public class DeckDetailController implements Serializable {
    @Autowired
    private DeckService deckService;
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private UserService userService;
    private Deck deck;
    private List<Card> cards;


    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void loadDeck(Long id) {
        if(this.deck==null) {
            if (id == null) {//create deck
                this.deck = new Deck();
                User user = userService.loadUser(sessionInfoBean.getCurrentUserName());
                this.deck.setCreator(user);
                this.deck.setStatus(DeckStatus.PRIVATE);
                user.getCreatedDecks().add(this.deck);
                this.cards = new ArrayList<>();
            }
            else{
            this.deck = this.deckService.loadDeck(id);
            this.cards = this.deck.getContent();
            }
        }
    }

    public void doSaveDeck() {
        this.deck.setContent(this.cards);
        this.deckService.saveDeck(this.deck);
    }

    public void doDeleteDeck() {
        this.deckService.deleteDeck(deck, sessionInfoBean.getCurrentUser());
        this.deck = null;
    }

    public void addCard() {
        Card card = new Card();
        card.setContainer(this.deck);
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }


}
