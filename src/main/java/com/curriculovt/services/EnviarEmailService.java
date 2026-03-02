package com.curriculovt.services;

import com.curriculovt.models.User;
import com.curriculovt.repositorys.UserRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class EnviarEmailService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        final String username = "curriculovt.contato@gmail.com";
        final String appPassword = "qmqjjwnahemyqunp";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setText(mensagem);

            Transport.send(message);
            System.out.println("Email enviado para: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Erro no envio: " + e.getMessage());
        }
    }

    public String gerarNovaSenha() {
        String letrasMin = "abcdefghijklmnopqrstuvwxyz";
        String letrasMai = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeros = "0123456789";
        String especiais = "!@#$%&";

        String todos = letrasMin + letrasMai + numeros + especiais;
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();

        senha.append(letrasMai.charAt(random.nextInt(letrasMai.length())));
        senha.append(numeros.charAt(random.nextInt(numeros.length())));
        senha.append(especiais.charAt(random.nextInt(especiais.length())));

        for (int i = 0; i < 5; i++) {
            senha.append(todos.charAt(random.nextInt(todos.length())));
        }

        List<Character> lista = senha.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(lista);

        return lista.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public void resetarSenha(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email nao encontrado no sistema."));

        String senhaLimpa = gerarNovaSenha();

        user.setPassword(passwordEncoder.encode(senhaLimpa));
        user.setSenhaRedefinidaPorEmail(true);

        userRepository.save(user);

        String assunto = "Reset de Senha - Curriculo VT";
        String mensagem = "Ola, " + user.getNome() + "\n\n" +
                "Voce solicitou a recuperacao de senha para sua conta no Curriculo VT.\n\n" +
                "Sua senha temporaria e: " + senhaLimpa + "\n\n" +
                "Importante: Ao fazer login, o sistema solicitara que voce crie uma nova senha definitiva.\n\n" +
                "Atenciosamente,\nEquipe Curriculo VT";

        enviarEmail(email, assunto, mensagem);
    }
}