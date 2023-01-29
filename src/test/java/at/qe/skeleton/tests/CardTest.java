package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.UserCardInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
public class CardTest {
    Card card;

    @Test
    public void testGetCardInfoReturnObjectThatWasSet() {
        card = new Card();
        Set<UserCardInfo> cardInfoSet = new HashSet<UserCardInfo>();
        card.setCardInfos(cardInfoSet);
        assertEquals(cardInfoSet, card.getCardInfos());
        assertEquals(0, cardInfoSet.size());
    }

    @Test
    public void testGetFrontSideIsSameAsSetFrontSide(){
        card = new Card();
        String textToStandOnFrontSide = "Here could stand your own card text.";
        card.setFrontSideContent(textToStandOnFrontSide);
        assertEquals(0, textToStandOnFrontSide.compareTo(card.getFrontSideContent()));
    }

    @Test
    public void testGetBackSideIsSameAsSetBackSide(){
        card = new Card();
        String textToStandOnBackSide = "Even on the back there is plenty of place.";
        card.setBackSideContent(textToStandOnBackSide);
        assertEquals(0, textToStandOnBackSide.compareTo(card.getBackSideContent()));
    }

    @Test
    public void testSetAndGetCreationDateIsTheDateSet(){
        card = new Card();
        Date date = new Date();
        long dateTime = date.getTime();
        card.setCreationDate(date);
        assertEquals(date, card.getCreationDate());
        assertEquals(dateTime, card.getCreationDate().getTime());
    }

    @Test
    public void testCardAreEqualIfIdIsTheSame_IdDifferent(){
        Card card1 = new Card();
        Card card2 = new Card();
        card1.setCardInfos(new HashSet<>());
        card2.setCardInfos(new HashSet<>());
        card1.setCreationDate(new Date());
        card2.setCreationDate(null);
        card1.setCardId(1L);
        card2.setCardId(2L);
        assertNotEquals(0, card1.compareTo(card2));
    }

    @Test
    public void testCardAreEqualIfIdIsTheSame_IdEqual(){
        Card card1 = new Card();
        Card card2 = new Card();
        card1.setCardInfos(new HashSet<>());
        card2.setCardInfos(new HashSet<>());
        card1.setCreationDate(new Date());
        card2.setCreationDate(null);
        card1.setCardId(1L);
        card2.setCardId(1L);
        assertEquals(0, card1.compareTo(card2));
    }
}
