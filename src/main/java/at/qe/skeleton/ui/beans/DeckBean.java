package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Viewscoped bean to cache and retrieve Decks
 * Also supplies a globalFilterFunction for Primefaces Datatable
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" by the
 * University of Innsbruck.
 */
@Component
@ViewScoped
public class DeckBean implements Serializable {

    private List<Deck> personalDecks;
    private List<Deck> savedDecks;
    private List<Deck> filteredPersonalDecks;
    private List<Deck> filteredSavedDecks;


    @PostConstruct
    public void init() {//TODO: get decks from service
        this.personalDecks = new ArrayList<>();
        this.savedDecks = new ArrayList<>();
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        User user = new User();
        user.setUsername("john");
        deck1.setDescription("deck-description 1");
        deck1.setTitle("Deck1");
        deck1.setStatus(DeckStatus.PUBLIC);
        deck1.setCreator(user);
        deck2.setDescription("deck-description 2Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        deck2.setTitle("Deck2");
        deck2.setStatus(DeckStatus.PRIVATE);
        personalDecks.add(deck1);
        personalDecks.add(deck2);
        savedDecks.add(deck1);
        savedDecks.add(deck2);

    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filter == null || filter == "") {
            return true;
        }
        Deck deck = (Deck) value;
        return deck.getTitle().toLowerCase().contains(filterText) || deck.getDescription().toLowerCase().contains(filterText);
    }

    public List<Deck> getPersonalDecks() {
        return personalDecks;
    }

    public void setPersonalDecks(List<Deck> personalDecks) {
        this.personalDecks = personalDecks;
    }

    public List<Deck> getSavedDecks() {
        return savedDecks;
    }

    public void setSavedDecks(List<Deck> savedDecks) {
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

    public List<Deck> getDecks(int i) { //get list of cached decks associated to specific tab i

        if (i == 0) {
            return getPersonalDecks();
        } else if (i == 1) {
            return getSavedDecks();
        } else {
            throw new IllegalArgumentException();
        }
    }

}
