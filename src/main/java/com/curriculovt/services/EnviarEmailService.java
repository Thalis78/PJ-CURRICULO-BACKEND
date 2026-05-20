package com.curriculovt.services;

import com.curriculovt.models.Preco;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.UserRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class EnviarEmailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrecoService precoService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final String username = "curriculovt.contato@gmail.com";
    private final String appPassword = "qmqjjwnahemyqunp";

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
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
            message.setContent(mensagem, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email enviado para: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Erro no envio: " + e.getMessage());
        }
    }

    public void enviarEmailTemplatePadrao(String destinatario, String assunto, String tituloPrincipal, String textoCorpo, String textoBotao, String linkBotao) {

        String htmlBotao = "";
        if (textoBotao != null && !textoBotao.trim().isEmpty()) {
            htmlBotao = "<div style='text-align: center; margin: 35px 0;'>" +
                    "  <a href='" + linkBotao + "' style='display: inline-block; background-color: #2563eb; color: #ffffff; padding: 14px 35px; border-radius: 50px; text-decoration: none; font-weight: bold; font-size: 15px; box-shadow: 0 4px 10px rgba(37, 99, 235, 0.25);'>" + textoBotao + "</a>" +
                    "</div>";
        }

        String corpoHtml = "<!doctype html>" +
                "<html lang='pt-br'>" +
                "  <head>" +
                "    <meta charset='UTF-8' />" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "  </head>" +
                "  <body style='margin: 0; padding: 0; background-color: #f9fafb; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>" +
                "    <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "      <tr>" +
                "        <td align='center' style='padding: 40px 10px'>" +
                "          <div style='max-width: 520px; width: 100%; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); border: 1px solid #e5e7eb;'>" +
                "            " +
                "            <!-- TOPO / LOGO -->" +
                "            <div style='background-color: #ffffff; padding: 30px 20px; text-align: center; border-bottom: 1px solid #f1f5f9;'>" +
                "              <div style='display: inline-block; vertical-align: middle; line-height: 1;'>" +
                "                <span style='color: #18181b; font-size: 22px; font-weight: bold; letter-spacing: -0.5px; text-transform: uppercase;'>CURRÍCULO</span>" +
                "                <span style='color: #2563eb; font-size: 22px; font-weight: 900; font-style: italic; letter-spacing: -1px; margin-left: 4px;'>VT</span>" +
                "              </div>" +
                "            </div>" +
                "            " +
                "            <!-- CONTEÚDO -->" +
                "            <div style='padding: 40px 35px; color: #374151'>" +
                "              <h2 style='margin: 0 0 20px 0; color: #111827; font-size: 22px; font-weight: 700; text-align: center;'>" + tituloPrincipal + "</h2>" +
                "              " +
                "              <p style='margin: 0 0 20px 0; font-size: 15px; line-height: 1.6; color: #4b5563; text-align: left;'>" +
                "                " + textoCorpo + "" +
                "              </p>" +
                "              " +
                "              " + htmlBotao + " " +
                "            </div>" +
                "            " +
                "            <!-- RODAPÉ -->" +
                "            <div style='background-color: #f9fafb; padding: 25px; text-align: center; border-top: 1px solid #e5e7eb;'>" +
                "              <p style='margin: 0; font-size: 12px; color: #9ca3af; line-height: 1.5;'>Este é um e-mail automático enviado pelo nosso sistema.</p>" +
                "              <p style='margin: 12px 0 0 0; font-size: 11px; color: #6b7280; font-weight: bold; text-transform: uppercase; letter-spacing: 1px;'>" +
                "                Equipe Currículo <span style='color: #2563eb'>VT</span>" +
                "              </p>" +
                "            </div>" +
                "            " +
                "          </div>" +
                "        </td>" +
                "      </tr>" +
                "    </table>" +
                "  </body>" +
                "</html>";

        enviarEmail(destinatario, assunto, corpoHtml);
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
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado no sistema."));

        String senhaLimpa = gerarNovaSenha();

        user.setPassword(passwordEncoder.encode(senhaLimpa));
        user.setSenhaRedefinidaPorEmail(true);

        userRepository.save(user);

        String assunto = "Recuperação de Senha - Currículo VT";

        String corpoHtml = "<!doctype html>" +
                "<html lang='pt-br'>" +
                "  <head>" +
                "    <meta charset='UTF-8' />" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "  </head>" +
                "  <body style='margin: 0; padding: 0; background-color: #f9fafb; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>" +
                "    <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "      <tr>" +
                "        <td align='center' style='padding: 40px 10px'>" +
                "          <div style='max-width: 500px; width: 100%; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); border: 1px solid #e5e7eb;'>" +
                "            <div style='background-color: #ffffff; padding: 35px 20px; text-align: center; border-bottom: 1px solid #f1f5f9;'>" +
                "              <div style='display: inline-block; vertical-align: middle; line-height: 1;'>" +
                "                <span style='color: #18181b; font-size: 22px; font-weight: bold; letter-spacing: -0.5px; text-transform: uppercase;'>CURRÍCULO</span>" +
                "                <span style='color: #2563eb; font-size: 22px; font-weight: 900; font-style: italic; letter-spacing: -1px; margin-left: 4px;'>VT</span>" +
                "              </div>" +
                "            </div>" +
                "            <div style='padding: 40px 35px; color: #374151'>" +
                "              <h2 style='margin: 0 0 15px 0; color: #111827; font-size: 22px; font-weight: 700; text-align: center;'>Olá, " + user.getNome() + "!</h2>" +
                "              <p style='margin: 0 0 25px 0; font-size: 16px; line-height: 1.6; color: #4b5563; text-align: center;'>" +
                "                Recebemos uma solicitação para redefinir sua senha. Utilize o código temporário abaixo para acessar sua conta:" +
                "              </p>" +
                "              <div style='background-color: #f8fafc; padding: 25px; border-radius: 12px; text-align: center; margin-bottom: 30px; border: 2px dashed #cbd5e1;'>" +
                "                <div style='font-size: 11px; color: #6b7280; text-transform: uppercase; margin-bottom: 10px; font-weight: 700; letter-spacing: 0.1em;'>Sua senha temporária</div>" +
                "                <div style='font-family: monospace; font-size: 24px; font-weight: bold; color: #2563eb;'>" + senhaLimpa + "</div>" +
                "              </div>" +
                "              <div style='background-color: #eff6ff; padding: 16px; border-radius: 8px; border-left: 4px solid #2563eb; margin-bottom: 30px;'>" +
                "                <p style='margin: 0; font-size: 14px; line-height: 1.5; color: #1e40af;'>" +
                "                  <strong>Atenção:</strong> Por motivos de segurança, ao realizar o próximo login, o sistema exigirá que você cadastre uma nova senha definitiva." +
                "                </p>" +
                "              </div>" +
                "              <div style='text-align: center'>" +
                "                <a href='https://curriculovt.com.br/login' style='display: inline-block; background-color: #2563eb; color: #ffffff; padding: 14px 35px; border-radius: 50px; text-decoration: none; font-weight: bold; font-size: 15px; box-shadow: 0 4px 10px rgba(37, 99, 235, 0.25);'>Acessar login</a>" +
                "              </div>" +
                "            </div>" +
                "            <div style='background-color: #f9fafb; padding: 25px; text-align: center; border-top: 1px solid #e5e7eb;'>" +
                "              <p style='margin: 0; font-size: 12px; color: #9ca3af; line-height: 1.5;'>Se você não solicitou esta mudança, pode ignorar este e-mail com segurança.</p>" +
                "              <p style='margin: 12px 0 0 0; font-size: 11px; color: #6b7280; font-weight: bold; text-transform: uppercase; letter-spacing: 1px;'>" +
                "                Equipe Currículo <span style='color: #2563eb'>VT</span>" +
                "              </p>" +
                "            </div>" +
                "          </div>" +
                "        </td>" +
                "      </tr>" +
                "    </table>" +
                "  </body>" +
                "</html>";

        enviarEmail(email, assunto, corpoHtml);
    }

    public void enviarLembretePagamento(User user) {
        Preco precoAtual = precoService.buscarConfiguracao();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFinalFormatado = nf.format(precoAtual.getValorFinal());

        String assunto = "Importante: Continue com seu Currículo VT";

        String corpoHtml = "<!doctype html>" +
                "<html lang='pt-br'>" +
                "  <head>" +
                "    <meta charset='UTF-8' />" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "  </head>" +
                "  <body style='margin: 0; padding: 0; background-color: #f9fafb; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>" +
                "    <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "      <tr>" +
                "        <td align='center' style='padding: 40px 10px'>" +
                "          <div style='max-width: 550px; width: 100%; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); border: 1px solid #e5e7eb;'>" +
                "            <div style='background-color: #ffffff; padding: 30px 20px; text-align: center; border-bottom: 1px solid #f1f5f9;'>" +
                "              <div style='display: inline-block; vertical-align: middle; line-height: 1;'>" +
                "                <span style='color: #18181b; font-size: 22px; font-weight: bold; letter-spacing: -0.5px; text-transform: uppercase;'>CURRÍCULO</span>" +
                "                <span style='color: #2563eb; font-size: 22px; font-weight: 900; font-style: italic; letter-spacing: -1px; margin-left: 4px;'>VT</span>" +
                "              </div>" +
                "            </div>" +
                "            <div style='padding: 35px; color: #374151'>" +
                "              <h2 style='margin: 0 0 20px 0; color: #111827; font-size: 20px; font-weight: 700;'>Olá, " + user.getNome() + "!</h2>" +
                "              <p style='margin: 0 0 15px 0; font-size: 15px; line-height: 1.6; color: #4b5563;'>" +
                "                Você já deu o primeiro passo ao criar sua conta — agora está a poucos minutos de ter um currículo profissional pronto para enviar e se destacar nas vagas que deseja." +
                "              </p>" +
                "              <p style='margin: 0 0 15px 0; font-size: 15px; line-height: 1.6; color: #4b5563;'>" +
                "                Com o <strong>Currículo VT</strong>, você não precisa perder tempo com formatação ou design. Nossa plataforma faz tudo isso automaticamente por apenas <strong>" + valorFinalFormatado + "</strong>." +
                "              </p>" +
                "              <div style='background-color: #eff6ff; padding: 20px; border-radius: 12px; margin: 25px 0; border-left: 4px solid #2563eb;'>" +
                "                <p style='margin: 0; font-size: 14px; color: #1e40af; line-height: 1.5;'>" +
                "                  <strong>Vantagens:</strong> Liberação instantânea via PIX, acesso total por 31 dias e garantia de satisfação ou seu dinheiro de volta." +
                "                </p>" +
                "              </div>" +
                "              <div style='text-align: center; margin-bottom: 30px;'>" +
                "                <a href='https://curriculovt.com.br/login' style='display: inline-block; background-color: #2563eb; color: #ffffff; padding: 16px 40px; border-radius: 50px; text-decoration: none; font-weight: bold; font-size: 16px; box-shadow: 0 4px 10px rgba(37, 99, 235, 0.25);'>Garantir meu acesso agora</a>" +
                "              </div>" +
                "              <p style='margin: 0; font-size: 13px; line-height: 1.6; color: #6b7280; font-style: italic; text-align: center;'>" +
                "                Lembrete: sua conta será excluída do nosso sistema em 15 dias caso opte por não fazer o currículo conosco nesse momento." +
                "              </p>" +
                "            </div>" +
                "            <div style='background-color: #f9fafb; padding: 25px; text-align: center; border-top: 1px solid #e5e7eb;'>" +
                "              <p style='margin: 0; font-size: 11px; color: #9ca3af; text-transform: uppercase; letter-spacing: 1px; font-weight: bold;'>" +
                "                Equipe Currículo <span style='color: #2563eb'>VT</span>" +
                "              </p>" +
                "            </div>" +
                "          </div>" +
                "        </td>" +
                "      </tr>" +
                "    </table>" +
                "  </body>" +
                "</html>";

        enviarEmail(user.getEmail(), assunto, corpoHtml);
    }
}