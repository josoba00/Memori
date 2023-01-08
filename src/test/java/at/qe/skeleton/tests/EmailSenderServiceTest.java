package at.qe.skeleton.tests;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.EmailSenderService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
class EmailSenderServiceTest {
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("admin", "passwd"))
        .withPerMethodLifecycle(true);
    
    @Autowired
    private EmailSenderService autowiredEmailSenderService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    
    @Autowired JavaMailSender autowiredJavaMailSender;
    User validUser;
    User userWithNoFirstName;
    User userWithNullEmail;
    String VALID_SUBJECT = "Test subject";
    String VALID_CONTENT = "This is some content";
    String VALID_HTML_CONTENT = """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="utf-8">
            <title>title</title>
          </head>
          <body>
            <!-- page content -->
          </body>
        </html>""";
    Deck deckByValidUser;
    Deck deckByUserWithNoFirstName;
    Deck deckByValidUserWithNoTitle;
    Deck deckByUserWithNoEmail;
    
    @Mock
    Logger spyableLogger;
    @Mock
    JavaMailSender mockJavaMailSender;
    @Mock
    FreeMarkerConfigurer mockConfigurer;
    @Mock
    Configuration mockFreemarkerConfig;
    EmailSenderService mockEmailSenderService;
    
    @Value("${spring.mail.username}")
    String sender;
    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setUsername("mockusername");
        validUser.setFirstName("mockfirstname");
        validUser.setEmail("mockuser@mail.com");
        
        userWithNullEmail = new User();
        userWithNullEmail.setUsername("mockusernamenull");
        userWithNullEmail.setFirstName("mockfirstnamenull");
        userWithNullEmail.setEmail(null);
        
        deckByUserWithNoEmail = new Deck();
        deckByUserWithNoEmail.setId(20L);
        deckByUserWithNoEmail.setCreator(userWithNullEmail);
        deckByUserWithNoEmail.setTitle("mocktitle0");
        
        deckByValidUser = new Deck();
        deckByValidUser.setId(21L);
        deckByValidUser.setCreator(validUser);
        deckByValidUser.setTitle("mocktitle");
        
        userWithNoFirstName = new User();
        userWithNoFirstName.setUsername("mockusername2");
        userWithNoFirstName.setEmail("mockuser2@mail.com");
        
        deckByUserWithNoFirstName = new Deck();
        deckByUserWithNoFirstName.setId(22L);
        deckByUserWithNoFirstName.setCreator(userWithNoFirstName);
        deckByUserWithNoFirstName.setTitle("mocktitle2");
        
        
        deckByValidUserWithNoTitle = new Deck();
        deckByValidUserWithNoTitle.setId(23L);
        deckByValidUserWithNoTitle.setCreator(validUser);
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendMessage_withValidParameters_setsCorrectRecipientEmailAddress() throws MessagingException {
        String expectedEmail = validUser.getEmail();
        autowiredEmailSenderService.sendMessage(validUser, VALID_SUBJECT, VALID_CONTENT);
        String emailSetByMethod = greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString();
        assertEquals(expectedEmail, emailSetByMethod, "Sent message does not contain the correct recipient");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendMessage_withValidParameters_sendsCorrectContent() {
        autowiredEmailSenderService.sendMessage(validUser, VALID_SUBJECT, VALID_CONTENT);
        String receivedMessageString = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
        assertEquals(VALID_CONTENT, receivedMessageString, "Sent content is not equal to expected");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendMessage_withInvalidUserEmail_shouldThrowNullPointerException() {
        Executable when = () -> autowiredEmailSenderService.sendMessage(userWithNullEmail, VALID_SUBJECT, VALID_CONTENT);
        assertThrows(NullPointerException.class, when, "sendDeckLockMessage does not throw NullPointerException if a Deck where creator.firstName is null is passed.");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendMessage_withMailExceptionThrown_shouldLogAsExpected() {
        mockEmailSenderService = new EmailSenderService(mockJavaMailSender, mockConfigurer, spyableLogger);
        doThrow(mock(MailException.class)).when(mockJavaMailSender).send(any(SimpleMailMessage.class));
        
        mockEmailSenderService.sendMessage(validUser, VALID_SUBJECT, VALID_CONTENT);
        
        verify(spyableLogger).error(any(String.class), any(User.class), any(String.class), any(String.class), any(MailException.class));
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendHtmlMessage_withMailExceptionThrown_shouldLogAsExpected() {
        mockEmailSenderService = new EmailSenderService(mockJavaMailSender, mockConfigurer, spyableLogger);
        ReflectionTestUtils.setField(mockEmailSenderService, "sender", sender);
        doThrow(mock(MailException.class)).when(mockJavaMailSender).send(any(MimeMessage.class));
        when(mockJavaMailSender.createMimeMessage()).thenReturn(autowiredJavaMailSender.createMimeMessage());
        
        mockEmailSenderService.sendHtmlEmail(validUser, VALID_SUBJECT, VALID_HTML_CONTENT);
        
        verify(spyableLogger).error(any(String.class), any(User.class), any(String.class), any(String.class), any(MailException.class));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendHtmlMessage_withValidParameters_setsCorrectRecipientEmailAddress() throws MessagingException {
        String expectedEmail = validUser.getEmail();
        autowiredEmailSenderService.sendHtmlEmail(validUser, VALID_SUBJECT, VALID_HTML_CONTENT);
        String emailSetByMethod = greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString();
        assertEquals(expectedEmail, emailSetByMethod, "Sent message does not contain the correct recipient");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendHtmlMessage_withValidParameters_sendsCorrectContent() {
        autowiredEmailSenderService.sendHtmlEmail(validUser, VALID_SUBJECT, VALID_HTML_CONTENT);
        String receivedMessageString = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]).replaceAll("\\s", "");
        assertTrue(receivedMessageString.contains(VALID_HTML_CONTENT.replaceAll("\\s", "")), "Sent content is not equal to expected");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendHtmlMessage_withInvalidUserEmail_shouldLogIllegalArgumentException() {
        mockEmailSenderService = new EmailSenderService(autowiredJavaMailSender, freeMarkerConfigurer, spyableLogger);
        ReflectionTestUtils.setField(mockEmailSenderService, "sender", sender);
    
        mockEmailSenderService.sendHtmlEmail(userWithNullEmail, VALID_SUBJECT, VALID_HTML_CONTENT);
    
        verify(spyableLogger).error(any(String.class), any(User.class), any(String.class), any(String.class), any(IllegalArgumentException.class));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendDeckLockMessage_withExceptionThrown_shouldLogAsExpected() throws IOException {
        mockEmailSenderService = new EmailSenderService(mockJavaMailSender, mockConfigurer, spyableLogger);
        ReflectionTestUtils.setField(mockEmailSenderService, "sender", sender);
        when(mockJavaMailSender.createMimeMessage()).thenReturn(autowiredJavaMailSender.createMimeMessage());
        when(mockConfigurer.getConfiguration()).thenReturn(mockFreemarkerConfig);
        doThrow(TemplateNotFoundException.class).when(mockFreemarkerConfig).getTemplate(any(String.class));
        
        mockEmailSenderService.sendDeckLockMessage(deckByValidUser);
        
        verify(spyableLogger).error(any(String.class), any(TemplateNotFoundException.class));
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendDeckLockMessage_withValidDeck_SendsCorrectHTMLTemplate() throws Exception {
        
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userFirstName", validUser.getFirstName());
        templateModel.put("deckTitle", deckByValidUser.getTitle());
        
        Template freemarkerTemplate = freeMarkerConfigurer.getConfiguration()
            .getTemplate("deck_lock_mail_template.ftl");
        String expectedHtmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel)
            .replaceAll("\\s", ""); // it seems like there is an issue with line break types (\n \r\n) when testing on different OS
        
        autowiredEmailSenderService.sendDeckLockMessage(deckByValidUser);
        
        String receivedMessageString = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0])
            .replaceAll("\\s", "");
        
        assertTrue(receivedMessageString.contains(expectedHtmlBody), "Sent body does not contain expected template.");
        
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendDeckLockMessage_withValidDeck_setsCorrectRecipientEmailAddress() throws MessagingException {
        String expectedEmail = validUser.getEmail();
        autowiredEmailSenderService.sendDeckLockMessage(deckByValidUser);
        String emailSetByMethod = greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString();
        assertEquals(expectedEmail, emailSetByMethod, "Sent message does not contain the correct recipient");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendDeckLockMessage_withInvalidDeckWhereUserHasNoFirstName_shouldThrowNullPointerException() {
        Executable when = () -> autowiredEmailSenderService.sendDeckLockMessage(deckByUserWithNoFirstName);
        assertThrows(NullPointerException.class, when, "sendDeckLockMessage does not throw NullPointerException if a Deck where creator.firstName is null is passed.");
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void sendDeckLockMessage_withInvalidDeckWhereDeckHasNoTitle_shouldThrowNullPointerException() {
        Executable when = () -> autowiredEmailSenderService.sendDeckLockMessage(deckByValidUserWithNoTitle);
        assertThrows(NullPointerException.class, when, "sendDeckLockMessage does not throw NullPointerException if a Deck where deck.title is null is passed.");
    }
    
}