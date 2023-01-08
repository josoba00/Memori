package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

@Component
@Scope("view")

public class DeckListController implements Serializable {
    @Autowired
    private DeckService deckService;
    @Autowired
    private SessionInfoBean sessionInfoBean;

    public Collection<Deck> searchDecks(String search){
        return deckService.loadDecksBySearch(search);
    }
}
