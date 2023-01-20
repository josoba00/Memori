package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection ;
import java.util.List;
import java.util.Locale;

/**
 * Viewscoped bean to cache and retrieve Decks for home view
 * Also supplies a globalFilterFunction for Primefaces Datatable
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" by the
 * University of Innsbruck.
 */
@Component
@ViewScoped
public class DeckBean implements Serializable {
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private transient DeckService deckService;
    @Autowired
    private transient UserService userService;

    private List<Deck> personalDecks;
    private List <Deck> savedDecks;
    private List <Deck> filteredPersonalDecks;
    private List<Deck> filteredSavedDecks;


    @PostConstruct
    public void init() {
        this.personalDecks = new ArrayList<>(userService.loadUser(sessionInfoBean.getCurrentUserName()).getCreatedDecks());
        this.savedDecks = new ArrayList<>(userService.loadUser(sessionInfoBean.getCurrentUserName()).getBookmarks());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filter == null || filter == "") {
            return true;
        }
        Deck deck = (Deck) value;
        return deck.getTitle().toLowerCase().contains(filterText) || deck.getDescription().toLowerCase().contains(filterText);
    }

    public Collection <Deck> getPersonalDecks() {
        return personalDecks;
    }

    public void setPersonalDecks(List <Deck> personalDecks) {
        this.personalDecks = personalDecks;
    }

    public Collection <Deck> getSavedDecks() {
        return savedDecks;
    }

    public void setSavedDecks(List <Deck> savedDecks) {
        this.savedDecks = savedDecks;
    }

    public List<Deck> getFilteredPersonalDecks() {
        return filteredPersonalDecks;
    }

    public void setFilteredPersonalDecks(List<Deck> filteredPersonalDecks) {
        this.filteredPersonalDecks = filteredPersonalDecks;
    }

    public List<Deck> getFilteredSavedDecks() {
        return filteredSavedDecks;
    }



    public void setFilteredSavedDecks(List<Deck> filteredSavedDecks) {
        this.filteredSavedDecks = filteredSavedDecks;
    }
    public void deleteDeck(Deck deck){
        this.deckService.deleteDeck(deck, sessionInfoBean.getCurrentUser());
        this.personalDecks.remove(deck);
    }
    public void deleteBookmark(Deck deck){
        this.savedDecks.remove(deck);
        this.userService.deleteBookmark(sessionInfoBean.getCurrentUser(), deck);
        this.userService.saveUser(sessionInfoBean.getCurrentUser());
    }
    public void makeDeckPublic(Deck deck){
        this.deckService.setDeckStatusPublic(deck);
        this.deckService.saveDeck(deck);
    }
    public void makeDeckPrivate(Deck deck){
        this.deckService.setDeckStatusPrivate(deck);
        this.deckService.saveDeck(deck);
    }
}