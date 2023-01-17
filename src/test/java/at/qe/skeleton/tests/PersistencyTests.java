package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserCardInfo;
import at.qe.skeleton.repositories.CardRepository;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.repositories.UserCardInfoRepository;
import at.qe.skeleton.repositories.UserRepository;
import at.qe.skeleton.services.UserService;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class PersistencyTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    UserCardInfoRepository userCardInfoRepository;
    
    User user1;
    User user2;
    Deck deck1;
    Deck deck2;
    Card card1;
    UserCardInfo userCardInfoForCardOneUserOne;
    
    @BeforeEach
    void setUp() {
        user1 = userRepository.findFirstByUsername("user1");
        assertNotNull(user1, "unable to load test user1 from database");
        user2 = userRepository.findFirstByUsername("user2");
        assertNotNull(user2, "unable to load test user2 from database");
        deck1 = deckRepository.findById(1L);
        assertNotNull(deck1, "unable to load test deck1 from database");
        deck2 = deckRepository.findById(2L);
        assertNotNull(deck2, "unable to load test deck2 from database");
        card1 = cardRepository.findById(1L);
        assertNotNull(card1, "unable to load test card1 from database");
        userCardInfoForCardOneUserOne = userCardInfoRepository.findFirstByUserAndCard(user1, card1);
        assertNotNull(userCardInfoForCardOneUserOne, "unable to load test UserCardInfo from database");
    }
    
    @Test
    @DirtiesContext
    void user_deleting_deletesUser(){
        userRepository.delete(user1);
        assertNull(userRepository.findFirstByUsername(user1.getId()));
    }
    
    @Test
    @DirtiesContext
    void deck_deleting_deletesDeck(){
        deckRepository.delete(deck1);
        assertNull(deckRepository.findById(deck1.getId()));
    }
    
    @Test
    @DirtiesContext
    void card_deletingThroughContainer_deletesCard(){
        Deck container = card1.getContainer();
        assertNotNull(container, "the tested card is not associated with a container make sure the entry in data.sql is correct");
        container.getContent().remove(card1);
        deckRepository.save(container);
        assertNull(cardRepository.findById(card1.getId()));
    }
    
    @Test
    @DirtiesContext
    void userCardInfo_deleting_deletesUserCardInfo(){
        userCardInfoRepository.delete(userCardInfoForCardOneUserOne);
        assertNull(userCardInfoRepository.findFirstByUserAndCard(userCardInfoForCardOneUserOne.getUser(), userCardInfoForCardOneUserOne.getCard()));
    }
    
    @Test
    @DirtiesContext
    void userCardInfo_deleting_willNotDeleteCorrespondingUser(){
        userCardInfoRepository.delete(userCardInfoForCardOneUserOne);
        assertNotNull(userRepository.findFirstByUsername(userCardInfoForCardOneUserOne.getUser().getId()));
    }
    
    @Test
    @DirtiesContext
    void userCardInfo_deleting_willNotDeleteCorrespondingCard(){
        userCardInfoRepository.delete(userCardInfoForCardOneUserOne);
        assertNotNull(cardRepository.findById(userCardInfoForCardOneUserOne.getCard().getId()));
    }
    
    @Test
    @DirtiesContext
    void User_deleting_willDeleteCreatedDecks(){
        Set<Deck> createdDecks = user1.getCreatedDecks();
        assertFalse(createdDecks.isEmpty(), "user1 did not create any decks make sure a corresponding entry in data.sql can be found");
        
        userRepository.delete(user1);
    
        List<Deck> createdDecksAfterDeletion = deckRepository.findAllByCreator(user1);
        assertTrue(createdDecksAfterDeletion.isEmpty(), "created decks were not deleted");
    }
    
    @Test
    @DirtiesContext
    void Deck_deleting_willNotDeleteCreator(){
        User creator = deck1.getCreator();
        assertNotNull(creator, "deck1 does not have a creator (null)");
        
        deckRepository.delete(deck1);
    
        User creatorAfterDeletion = userRepository.findFirstByUsername(creator.getUsername());
        assertNotNull(creatorAfterDeletion, "creator was deleted when deck got deleted");
    }
    @Test
    @DirtiesContext
    void User_deleting_willNotDeleteBookmarkedDecks(){
        Set<Deck> bookmarks = user1.getBookmarks();
        assertFalse(bookmarks.isEmpty(), "user1 did not bookmark any decks make sure a corresponding entry in data.sql can be found");
        
        userRepository.delete(user1);
        
        List<Deck> bookmarkedDecks = bookmarks.stream().map(b -> deckRepository.getReferenceById(b.getId())).toList();
        assertFalse(bookmarkedDecks.isEmpty(), "bookmarks were not deleted");
        assertEquals(bookmarks.size(), bookmarkedDecks.size(), "some decks seem to have been deleted");
    }
    
    @Test
    @DirtiesContext
    void Deck_addingNewCard_cardWillBeContainedInDeck(){
        Card newCard = new Card();
        newCard.setContainer(deck2);
        deck2.getContent().add(newCard);
        deckRepository.save(deck2);
        int oldSize = deck2.getContent().size();
        assertEquals(oldSize, deckRepository.findById(deck2.getId()).getContent().size());
        
        
    }
    
    @Test
    @DirtiesContext
    void Deck_deleting_willNotDeleteUsersWhoBookmarkedTheDeck(){
        Set<User> bookmarkingUsers = deck2.getBookmarkedBy();
        assertFalse(bookmarkingUsers.isEmpty(), "no users bookmarked this deck make sure a corresponding entry in data.sql can be found");
        
        deckRepository.delete(deck2);
        
        bookmarkingUsers.forEach(u -> assertNotNull(userRepository.findFirstByUsername(u.getUsername()), "a bookmarking user was deleted"));
    }
    
    @Test
    @DirtiesContext
    void Deck_deleting_willDeleteAllCardsContainedInIt(){
        List<Card> containedCards = deck2.getContent();
        assertFalse(containedCards.isEmpty(), "no users bookmarked this deck make sure a corresponding entry in data.sql can be found");
        
        deckRepository.delete(deck2);
        
        containedCards.forEach(c -> assertNull(cardRepository.findById(c.getId()), "a bookmarking user was deleted"));
    }
    
    @Test
    @DirtiesContext
    void Deck_deleting_willDeleteBookmarksFromUsers(){
        Set<User> bookmarkingUsers = deck2.getBookmarkedBy();
        assertFalse(bookmarkingUsers.isEmpty(), "no users bookmarked this deck make sure a corresponding entry in data.sql can be found");
        
        deckRepository.delete(deck2);
        
        bookmarkingUsers.forEach(u -> assertFalse(userRepository.findFirstByUsername(u.getUsername()).getBookmarks().contains(deck2)));
        
    }
    
    @Test
    @DirtiesContext
    void User_deleting_willDeleteUserCardInfosOfUser() {
        List<UserCardInfo> userCardInfos = userCardInfoRepository.findAllByUser(user1);
        assertFalse(userCardInfos.isEmpty(), "user does not have any UserCardInfos make sure a corresponding entry in data.sql can be found");
        
        userRepository.delete(user1);
        
        assertTrue(userCardInfoRepository.findAllByUser(user1).isEmpty(), "there still exist UserCardInfos for the user after deleting the user");
        
    }
    
    @Test
    @DirtiesContext
    void Card_deleting_willDeleteUserCardInfosOfCard() {
        List<UserCardInfo> userCardInfos = userCardInfoRepository.findAllByCard(card1);
        assertFalse(userCardInfos.isEmpty(), "user does not have any UserCardInfos make sure a corresponding entry in data.sql can be found");
        
        cardRepository.delete(card1);
        
        assertTrue(userCardInfoRepository.findAllByCard(card1).isEmpty(), "there still exist UserCardInfos for the user after deleting the user");
        
    }
}
