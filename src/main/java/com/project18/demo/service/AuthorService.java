package com.project18.demo.service;

import com.project18.demo.exception.PasswordRepeatIncorrectException;
import com.project18.demo.model.Authors;
import com.project18.demo.model.Role;
import com.project18.demo.model.dto.UserDTO;
import com.project18.demo.repository.AuthorsRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorService {

    private static String ADDRESS_URL = "/signup";
    private final AuthorsRepository authorsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    @Value("${self.app-site}")
    private String appSite;

    @Autowired
    public AuthorService(AuthorsRepository authorsRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.authorsRepository = authorsRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Transactional
    public void register(UserDTO user) {
        if (!user.getPassword().equals(user.getRe_password())) {
            throw new PasswordRepeatIncorrectException("Hasła nie są zgodne", "password");
        }
        Authors author = Authors.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .verificationCode(RandomString.make(64))
                .role(Role.USER)
                .enabled(false)
                .build();
        Authors save = authorsRepository.save(author);
        sendVerificationEmail(save, appSite+ADDRESS_URL);
    }

    private void sendVerificationEmail(Authors author, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = author.getEmail();
        String fromAddress = "ugtjeapp@yahoo.com";
        String senderName = "Blog A.J.";
        String subject = "Zweryfikuj swoją rejestrację na moim blogu";
        String content = "Dear [[name]],<br>"
                + "Kliknij w poniższy link, żeby dokończyć proces rejestracji:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Dziękuję,<br>"
                + "Twój ulubiony blog :).";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(new InternetAddress(toAddress));
        helper.setSubject(subject);

        String verifyURL = siteURL + "/verify?code=" + author.getVerificationCode();
        log.info("Prepared link to activate user {} ", verifyURL);
        content = content.replace("[[URL]]", verifyURL);
        content = content.replace("[[name]]", author.getFullName());
        helper.setText(content, true);
        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        var author = authorsRepository.findAll().stream().filter(it -> it.getVerificationCode().equals(verificationCode)).collect(Collectors.toList()).stream().findAny();

        if (author.isEmpty() || author.get().isEnabled()) {
            return false;
        } else {
            Authors authors = author.get();
            authors.setVerificationCode(null);
            authors.setEnabled(true);
            authorsRepository.save(authors);
            return true;
        }
    }

    /*public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        User user = repo.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            repo.save(user);
        } else {
            throw new UserNotFoundException("Nie znaleziono użytkownika z tym adresem e-mail : " + email);
        }
    }

    public User getByResetPasswordToken(String token) {
        return repo.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        repo.save(user);
    }*/

    public List<Authors> getAll() {
        return authorsRepository.findAll();
    }
}
