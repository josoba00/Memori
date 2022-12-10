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
public class Card implements CardInterface, Persistable<Long>, Serializable, Comparable<Card> {
  @Id
  private Long id;
  
  private String frontSideContent;
  private String backSideContent;
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;
  
  @ManyToOne(
      fetch = FetchType.LAZY
  )
  private Deck container;
  
  @OneToMany(fetch = FetchType.LAZY)
  private Set<UserCardInfo> cardInfos;
  
  // used in compareTo to evade null return
  private Long getCardId() {
    return id;
  }
  
  public Deck getContainer() {
    return container;
  }
  
  public void setContainer(Deck container) {
    this.container = container;
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
  
  public Set<UserCardInfo> getCardInfos() {
    return cardInfos;
  }
  
  public void setCardInfos(Set<UserCardInfo> cardInfos) {
    this.cardInfos = cardInfos;
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
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
  
  @Override
  public Long getId() {
    return getCardId();
  }
  
  public void setId(Long cardId) {
    this.id = cardId;
  }
  
  @Override
  public String toString() {
    return "at.qe.skeleton.model.Card[ id=" + getId() + " ]";
  }
  
  @Override
  public boolean isNew() {
    return false;
  }
}
