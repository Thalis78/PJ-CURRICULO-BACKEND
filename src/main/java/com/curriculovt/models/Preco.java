package com.curriculovt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "precos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Preco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer percentualDesconto;

    public BigDecimal getValorFinal() {
        if (percentualDesconto == null || percentualDesconto == 0) {
            return valorBase.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal fator = BigDecimal.ONE.subtract(
                new BigDecimal(percentualDesconto).divide(new BigDecimal(100))
        );
        BigDecimal bruto = valorBase.multiply(fator);

        BigDecimal step = new BigDecimal("0.05");
        return bruto.divide(step, 0, RoundingMode.HALF_UP)
                .multiply(step)
                .setScale(2, RoundingMode.HALF_UP);
    }
}