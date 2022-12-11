package at.qe.skeleton.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserCardInfoID implements Serializable {
  
    
   private Long cardId;
   @Column(length = 100)
   private String username;
    
    public UserCardInfoID() {}
    
    public UserCardInfoID(Long cardId, String username) {
        this.cardId = cardId;
        this.username = username;
    }
    
    public Long getCardId() {
        return cardId;
    }
    
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public int hashCode() {
        return cardId.hashCode() + username.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserCardInfoID)) {
            return false;
        }
        UserCardInfoID other = (UserCardInfoID) obj;
        return this.cardId.equals(other.cardId) &&
            this.username.equals(other.username);
    }
    
    @Override
    public String toString() {
        return "at.qe.skeleton.model.UserCardInfoID[ " + this.cardId.toString() + " " + this.username + "]";
    }
}
