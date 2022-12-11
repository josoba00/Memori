package at.qe.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Viewscoped bean to retrieve decks.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" by the
 * University of Innsbruck.
 */
@Component
@ViewScoped
public class DeckBean implements Serializable {

    private List<Object> personalDecks;
    private List<Object> savedDecks;
    private List<Object> filteredPersonalDecks; // TODO: change to <Deck>
    private List<Object> filteredSavedDecks; // TODO: change to <Deck>


    @PostConstruct
    public void init(){
        this.personalDecks= new ArrayList<>(); //TODO: get decks from service
        this.savedDecks= new ArrayList<>(); //TODO: get decks from service
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        return true; // TODO: create globalFilterFunction
    }

    public List<Object> getPersonalDecks() {
        return personalDecks;
    }

    public void setPersonalDecks(List<Object> personalDecks) {
        this.personalDecks = personalDecks;
    }

    public List<Object> getSavedDecks() {
        return savedDecks;
    }

    public void setSavedDecks(List<Object> savedDecks) {
        this.savedDecks = savedDecks;
    }

    public List<Object> getFilteredPersonalDecks() {
        return filteredPersonalDecks;
    }

    public void setFilteredPersonalDecks(List<Object> filteredPersonalDecks) {
        this.filteredPersonalDecks = filteredPersonalDecks;
    }

    public List<Object> getFilteredSavedDecks() {
        return filteredSavedDecks;
    }

    public void setFilteredSavedDecks(List<Object> filteredSavedDecks) {
        this.filteredSavedDecks = filteredSavedDecks;
    }
}
