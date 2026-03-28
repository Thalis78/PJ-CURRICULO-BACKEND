package com.curriculovt.services;

import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import com.curriculovt.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AccountCleanupTask {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnviarEmailService enviarEmailService;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void verificarContasInativas() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicioRegra = LocalDate.of(2026, 4, 15);

        if (hoje.isBefore(dataInicioRegra)) {
            return;
        }

        List<User> usuariosNaoPagos = userRepository.findByRoleAndPagamentoFalse(UserRole.COMMON);

        for (User user : usuariosNaoPagos) {
            LocalDateTime dataCriacao = user.getDataCriacaoConta();

            if (dataCriacao != null) {
                LocalDate dataCriacaoLocalDate = dataCriacao.toLocalDate();

                boolean mesmoDiaDoMes = hoje.getDayOfMonth() == dataCriacaoLocalDate.getDayOfMonth();
                long mesesPassados = ChronoUnit.MONTHS.between(dataCriacaoLocalDate, hoje);

                if (mesmoDiaDoMes && mesesPassados >= 1 && user.getDataExpiracao() == null) {
                    userRepository.delete(user);
                    System.out.println("Cron: Usuário " + user.getEmail() + " excluído (Criado em: " + dataCriacaoLocalDate + ")");
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarEnvioLembrete16Dias() {
        LocalDate hoje = LocalDate.now();
        List<User> usuariosNaoPagos = userRepository.findByRoleAndPagamentoFalse(UserRole.COMMON);

        for (User user : usuariosNaoPagos) {
            if (user.getDataCriacaoConta() != null) {
                LocalDate dataCriacao = user.getDataCriacaoConta().toLocalDate();
                long diasPassados = java.time.temporal.ChronoUnit.DAYS.between(dataCriacao, hoje);

                if (diasPassados == 16) {
                    enviarEmailService.enviarLembretePagamento(user);
                }
            }
        }
    }
}