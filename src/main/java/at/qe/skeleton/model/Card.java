package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Date;
import java.util.Set;


/**
 * Entity representation of a Learning card
 */

@Entity
@Table(name = "card")
public class Card implements CardInterface, Persistable<Long>, Serializable, Comparable<Card>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Deck container;

    private String frontSideContent;
    private String backSideContent;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @OneToMany(
        mappedBy = "card",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<UserCardInfo> cardInfos;
    private Long getCardId() {
        return id;
    }

    public Deck getContainer() {
        return container;
    }

    public void setContainer(Deck container) {
        this.container = container;
    }

    public Set<UserCardInfo> getCardInfos() {
        return cardInfos;
    }

    public void setCardInfos(Set<UserCardInfo> cardInfos) {
        this.cardInfos = cardInfos;
    }

    public void setCardId(Long cardId) {
        this.id = cardId;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(Card o) {
        return this.id.compareTo(o.getCardId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
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
        return Objects.equals(this.id, other.id);
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
        return null == creationDate;
    }
}
