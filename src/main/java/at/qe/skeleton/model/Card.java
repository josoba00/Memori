package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Date;


/**
 * Entity representation of a Learning card
 */

@Entity
public class Card implements CardInterface, Persistable<Integer>, Serializable, Comparable<Card>
{
    @Id
    private Integer cardId;

    private Integer deckId;

    private String frontSideContent;
    private String backSideContent;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getDeckId() {
        return deckId;
    }

    public void setDeckId(Integer deckId) {
        this.deckId = deckId;
    }

    public String getFrontSideContent() {
        return frontSideContent;
    }

    public void setFrontSideContent(String frontSideContent) {
        this.frontSideContent = frontSideContent;
    }

    public String getBackSideContent() {
        return backSideContent;
    }

    public void setBackSideContent(String backSideContent) {
        this.backSideContent = backSideContent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public int compareTo(Card o) {
        return this.cardId.compareTo(o.getCardId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.cardId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CardInterface)) {
            return false;
        }
        final Card other = (Card) obj;
        if (!Objects.equals(this.cardId, other.cardId)) {
            return false;
        }
        return true;
    }

    @Override
    public Integer getId() {
        return getCardId();
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.User[ id=" + getCardId() + " ]";
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
