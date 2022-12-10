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
@Table(name = "card")
public class Card implements CardInterface, Persistable<Long>, Serializable, Comparable<Card>
{
    @Id
    private Long cardId;

    private Long deckId;

    private String frontSideContent;
    private String backSideContent;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
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
        return Objects.equals(this.cardId, other.cardId);
    }

    @Override
    public Long getId() {
        return getCardId();
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.User[ id=" + getCardId() + " ]";
    }

    @Override
    public boolean isNew() {
        return null==createDate;
    }
}
