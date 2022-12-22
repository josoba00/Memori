package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    UserRepository userRepository;

    Deck copiedDeck = new Deck();


    @Test
    void findAllByCreator() {
        copiedDeck.setId(1L);

        assertEquals(List.of(copiedDeck), deckRepository.findAllByCreator(userRepository.findFirstByUsername("user1")));
    }

    @Test
    void findAllByStatusIsPublic() {
        copiedDeck.setStatus(DeckStatus.PUBLIC);
        copiedDeck.setId(2L);
        Deck copiedDeck02 = new Deck();
        copiedDeck02.setStatus(DeckStatus.PUBLIC);
        copiedDeck02.setId(3L);

        assertEquals(List.of(copiedDeck, copiedDeck02), deckRepository.findAllByStatusIsPublic());
    }

    @Test
    void findAllByTitleContaining() {
        copiedDeck.setId(1L);
        copiedDeck.setTitle("EN to GER Vocab");

        assertEquals(List.of(copiedDeck), deckRepository.findAllByTitleContaining("GER"));
    }
}