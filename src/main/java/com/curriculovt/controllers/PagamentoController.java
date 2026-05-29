package com.curriculovt.controllers;

import com.curriculovt.dtos.CheckoutRequestDTO;
import com.curriculovt.services.PagamentoService;
import com.curriculovt.services.UserService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private UserService userService;

    @PostMapping("/checkout")
    public ResponseEntity<String> realizarPagamento(@RequestBody CheckoutRequestDTO request) {
        String url = pagamentoService.gerarLinkDePagamento(request.getUsuarioId(), "pagamento", request.getPlano());
        return ResponseEntity.ok(url);
    }

    @PostMapping("/checkout-renovacao")
    public ResponseEntity<String> realizarRenovacao(@RequestBody CheckoutRequestDTO request) {
        String url = pagamentoService.gerarLinkDePagamento(request.getUsuarioId(), "sessao-expirada", request.getPlano());
        return ResponseEntity.ok(url);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> ouvirWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestParam(value = "data.id", required = false) String queryId
    ) {
        String action = (String) payload.get("action");
        String paymentIdStr = queryId;

        if (paymentIdStr == null && payload.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            paymentIdStr = String.valueOf(data.get("id"));
        }

        if (paymentIdStr != null && ("payment.updated".equals(action) || "payment.created".equals(action))) {
            try {
                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(paymentIdStr));

                String usuarioId = payment.getExternalReference();
                String status = payment.getStatus();

                if ("approved".equals(status) && usuarioId != null) {

                    String planoDias = "31";
                    if (payment.getMetadata() != null && payment.getMetadata().containsKey("plano_dias")) {
                        planoDias = String.valueOf(payment.getMetadata().get("plano_dias"));
                    }

                    System.out.println("PAGAMENTO APROVADO! Plano selecionado: " + planoDias + " dias. Usuário: " + usuarioId);

                    if ("15".equals(planoDias)) {
                        userService.adicionarQuinzeDiasDeAssinatura(Long.parseLong(usuarioId));
                    } else {
                        userService.adicionarMesDeAssinatura(Long.parseLong(usuarioId));
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar pagamento no webhook: " + e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }
}