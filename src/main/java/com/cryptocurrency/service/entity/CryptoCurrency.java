package com.cryptocurrency.service.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yuvaraj.sanjeevi
 */

@Getter
@Setter
@ToString
@Entity
@Table(name = "crypto_currencies")
public class CryptoCurrency {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "rank")
    private Long rank;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "rate_usd", precision = 25, scale = 16)
    private BigDecimal rateUSD;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptoCurrency that = (CryptoCurrency) o;

        return getCode().equals(that.getCode());

    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }

}
