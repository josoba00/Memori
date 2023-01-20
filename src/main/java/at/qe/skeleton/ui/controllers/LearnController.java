package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.LearnService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

@Controller
@Scope("view")
public class LearnController implements Serializable {
    @Autowired
    private transient SessionInfoBean sessionInfoBean;
    
    @Autowired
    private transient LearnService learnService;
    
    private User currentUser;
    
    private Queue<Card> cardsQueue = new LinkedList<>();
    
    private Queue<Card> neverLearnedCardsQueue = new LinkedList<>();
    
    private Deck learningDeck;
    private Card currentlyDisplayedCard;
    
    private String currentlyDisplayedText;
    
    private boolean isFrontSide;
    
    private int difficulty;
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    private void setCurrentUser() {
        this.currentUser = sessionInfoBean.getCurrentUser();
    }
    
    public enum InitialisationError {
        NO_CARDS_TO_LEARN,
        SUCCESS
    }
    
    /**
     * Method initializes Queue with Cards for today and never learned ones.
     *
     * @param deck from where to get the cards
     * @return NO_CARDS_TO_LEARN if both queues are empty aka no cards to learn.
     * SUCCESS if at least one card is found.
     */
    public InitialisationError doInitializeQueues(Deck deck) {
        this.setCurrentUser();
        this.learningDeck = deck;
        this.cardsQueue = new LinkedList<>(learnService.findCardsToLearn(deck.getContent(), currentUser));
        this.neverLearnedCardsQueue = new LinkedList<>(learnService.findNeverLearnedCards(deck.getContent(), currentUser));
        if (cardsQueue.isEmpty() && neverLearnedCardsQueue.isEmpty()) {
            return InitialisationError.NO_CARDS_TO_LEARN;
        }
        this.doGetNextCard();
        return InitialisationError.SUCCESS;
    }
    
    /**
     * Method gets next Card from Queue. First CardsToLearn then never learnedCards.
     *
     * @return next Card or null when queues are empty.
     */
    public Card doGetNextCard() {
        if (cardsQueue.isEmpty()) {
            if (neverLearnedCardsQueue.isEmpty()) {
                this.currentlyDisplayedCard = null;
                this.currentlyDisplayedText = "no cards left. well done!";
                return null;
            }
            this.currentlyDisplayedCard = neverLearnedCardsQueue.poll();
            this.isFrontSide = false;
            this.doGetOtherSideOfCard();
            return this.currentlyDisplayedCard;
        }
        this.currentlyDisplayedCard = cardsQueue.poll();
        this.isFrontSide = false;
        this.doGetOtherSideOfCard();
        return currentlyDisplayedCard;
    }
    
    public void doNext() {
        doAddCardBackQueue(currentlyDisplayedCard);
        doGetNextCard();
    }
    
    public enum LearningCardsEnum {
        HAS_NEXT_CARD,
        NO_NEXT_CARD
    }
    
    /**
     * Method checks if there is still a card to learn.
     *
     * @return HAS_NEXT_CARD if there is a card.
     * NO_NEXT_CARD if there is no next card.
     */
    public LearningCardsEnum hasNextCard() {
        return neverLearnedCardsQueue.isEmpty() && cardsQueue.isEmpty() ? LearningCardsEnum.NO_NEXT_CARD : LearningCardsEnum.HAS_NEXT_CARD;
    }
    
    public void doGetOtherSideOfCard() {
        this.currentlyDisplayedText = this.isFrontSide ?
            this.currentlyDisplayedCard.getBackSideContent() :
            this.currentlyDisplayedCard.getFrontSideContent();
        this.isFrontSide = !this.isFrontSide;
    }
    
    /**
     * Method adds card back into queue if necessary.
     *
     * @param card
     */
    public void doAddCardBackQueue(Card card) {
        if (card == null) {
            return;
        }
        updateRepository(card);
        if (this.difficulty < 4) {
            cardsQueue.add(card);
        }
    }
    
    /**
     * Method calls learnService to update the Repository entries.
     *
     * @param card
     */
    public void updateRepository(Card card) {
        learnService.updateUserCardInfo(card, this.currentUser, this.difficulty);
    }
    
    /**
     * Method determines amount of cards that have to be repeated in queue.
     *
     * @return amount
     */
    public int getCardsToLearnAmount() {
        return this.cardsQueue.size();
    }
    
    /**
     * Method determines amount of cards that have never been learned in queue.
     *
     * @return amount
     */
    public int getNeverLearnedCardsAmount() {
        return this.neverLearnedCardsQueue.size();
    }
    
    public int getTotalAmountCards() {
        return getCardsToLearnAmount() + getNeverLearnedCardsAmount();
    }
    
    public Deck getLearningDeck() {
        return learningDeck;
    }
    
    public void setLearningDeck(Deck learningDeck) {
        this.learningDeck = learningDeck;
    }
    
    public String getCurrentlyDisplayedText() {
        return currentlyDisplayedText;
    }
}
