package com.curriculovt.services;

import com.curriculovt.models.Preco;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PrecoService precoService;

    public PagamentoService(PrecoService precoService) {
        this.precoService = precoService;
    }

    public String gerarLinkDePagamento(String usuarioId, String contexto, String plano) {
        MercadoPagoConfig.setAccessToken("APP_USR-3675438486844546-031012-b96b0c111869bc2b0e8eab7d08a784b7-325277454");

        BigDecimal valorFinal;
        String titulo;
        String descricao;

        if ("15".equals(plano)) {
            valorFinal = new BigDecimal("0.05");
            titulo = "Plano Essencial - 15 Dias";
            descricao = "Acesso de 15 dias ao sistema de currículos";
        } else {
            Preco configuracaoPreco = precoService.buscarConfiguracao();
            valorFinal = new BigDecimal("0.10");
            titulo = "Plano Completo - 31 Dias";
            descricao = "Acesso de 31 dias ao sistema de currículos";
        }

        String backUrl = "https://curriculovt.com.br/" + contexto;

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("15".equals(plano) ? "essential_15" : "premium_31")
                .title(titulo)
                .description(descricao)
                .quantity(1)
                .unitPrice(valorFinal)
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrl)
                .pending(backUrl)
                .failure(backUrl)
                .build();

        List<PreferencePaymentTypeRequest> excludedTypes = new ArrayList<>();
        excludedTypes.add(PreferencePaymentTypeRequest.builder().id("credit_card").build());
        excludedTypes.add(PreferencePaymentTypeRequest.builder().id("debit_card").build());
        excludedTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());

        PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                .excludedPaymentTypes(excludedTypes)
                .installments(1)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("plano_dias", plano);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .externalReference(usuarioId)
                .metadata(metadata)
                .autoReturn("approved")
                .paymentMethods(paymentMethods)
                .build();

        PreferenceClient client = new PreferenceClient();

        try {
            Preference preference = client.create(preferenceRequest);
            return preference.getInitPoint();
        } catch (Exception e) {
            return "Erro ao gerar pagamento: " + e.getMessage();
        }
    }
}