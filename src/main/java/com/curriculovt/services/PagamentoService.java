package com.curriculovt.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PagamentoService {

    public String gerarLinkDePagamento() {
        MercadoPagoConfig.setAccessToken("APP_USR-3675438486844546-031012-b96b0c111869bc2b0e8eab7d08a784b7-325277454");

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("1234")
                .title("Plano Premium Curriculo_vt")
                .description("Criação de currículo profissional")
                .quantity(1)
                .unitPrice(new BigDecimal("0.05"))
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://curriculovt.com.br/sobre")
                .pending("https://curriculovt.com.br/sobre")
                .failure("https://curriculovt.com.br/pagamento")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
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