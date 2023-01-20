package at.qe.skeleton.services;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope("application")
public class EmailSenderService implements MessageSenderService {
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final Logger logger;
    @Value("${spring.mail.username}")
    private String sender;
    public EmailSenderService(JavaMailSender mailSender, FreeMarkerConfigurer freeMarkerConfigurer, Logger logger) {
        this.mailSender = mailSender;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.logger = logger;
    }
    
    
    @Override
    public void sendMessage(User to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        String recipientEmail = to.getEmail();
        message.setFrom(sender);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            logger.error("Unable to send SimpleMailMessage to: {} subject: {} content: {}", to, subject, content, e);
        }
    }
    
    
    public void sendHtmlEmail(User to, String subject, String htmlBody) {
        String recipientEmail = to.getEmail();
        try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(sender);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Unable to build MimeMessage to: {} subject: {} htmlBody: {}", to, subject, htmlBody, e);
        } catch (Exception e) {
            logger.error("Unable to send MimeMessage to : {} subject: {} htmlBody: {}", to, subject, htmlBody, e);
        }
    }
    
    @Override
    public void sendDeckLockMessage(Deck deck) throws NullPointerException {
        User creator = deck.getCreator();
        String creatorFirstName = creator.getFirstName();
        String deckTitle = deck.getTitle();
        if (creatorFirstName == null || deckTitle == null) {
            throw new NullPointerException("Invalid deck: some value [deck.getCreator.getFirstName, deck.getTitle] is null");
        }
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userFirstName", creatorFirstName);
        templateModel.put("deckTitle", deckTitle);
    
        String htmlBody = null;
        try {
            Template freemarkerTemplate = freeMarkerConfigurer.getConfiguration()
                .getTemplate("deck_lock_mail_template.ftl");
            htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);
        } catch (Exception e) {
            logger.error("Failed to use template", e);
        }
    
        this.sendHtmlEmail(creator, "One of your decks has been locked", htmlBody);
        
        for (User bookmarkingUser : deck.getBookmarkedBy()) {
            String bookmarkingUserFirstName = bookmarkingUser.getFirstName();
            
            Map<String, Object> templateModelBookmarked = new HashMap<>();
            templateModelBookmarked.put("userFirstName", bookmarkingUserFirstName);
            templateModelBookmarked.put("deckTitle", deckTitle);
    
            String bookmarkedHtmlTemplate = null;
            try {
                Template freemarkerTemplate = freeMarkerConfigurer.getConfiguration()
                    .getTemplate("bookmarked_deck_lock_mail_template.ftl");
                bookmarkedHtmlTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModelBookmarked);
            } catch (Exception e) {
                logger.error("Failed to use template", e);
            }
            sendHtmlEmail(bookmarkingUser, "One of your bookmarked Decks has been locked", bookmarkedHtmlTemplate);
        }
    }
}
