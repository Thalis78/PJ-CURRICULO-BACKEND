package com.curriculovt.controllers;

import com.curriculovt.services.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService = new PagamentoService();

    @PostMapping("/checkout")
    public String realizarPagamento() {
        return pagamentoService.gerarLinkDePagamento();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> ouvirWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestParam(value = "data.id", required = false) String queryId
    ) {
        String action = (String) payload.get("action");
        String paymentId = queryId;

        if (paymentId == null && payload.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            paymentId = String.valueOf(data.get("id"));
        }

        if ("payment.updated".equals(action) || "payment.created".equals(action)) {
            System.out.println("Processando Webhook - ID: " + paymentId + " Ação: " + action);
        }

        return ResponseEntity.ok().build();
    }
}