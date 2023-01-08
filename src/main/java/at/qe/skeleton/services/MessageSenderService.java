package at.qe.skeleton.services;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import org.springframework.security.access.prepost.PreAuthorize;

public interface MessageSenderService {
    void sendMessage(User to, String subject, String content) throws NullPointerException;
    @PreAuthorize("hasAuthority('ADMIN')")
    void sendDeckLockMessage(Deck deck) throws NullPointerException;
}
