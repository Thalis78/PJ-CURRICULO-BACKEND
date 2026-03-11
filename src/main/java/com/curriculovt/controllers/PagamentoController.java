package com.curriculovt.controllers;

import com.curriculovt.dtos.CheckoutRequestDTO;
import com.curriculovt.services.PagamentoService;
import com.curriculovt.services.UserService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final UserService userService;

    public PagamentoController(PagamentoService pagamentoService, UserService userService) {
        this.pagamentoService = pagamentoService;
        this.userService = userService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> realizarPagamento(@RequestBody CheckoutRequestDTO request) {
        String url = pagamentoService.gerarLinkDePagamento(request.getUsuarioId());
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
                    userService.adicionarMesDeAssinatura(Long.parseLong(usuarioId));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }
}