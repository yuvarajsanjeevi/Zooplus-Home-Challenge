package com.cryptocurrency.service.repository;

import com.cryptocurrency.service.entity.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Repository Layer for {@link CryptoCurrency}
 * @author yuvaraj.sanjeevi
 */
@Repository
public interface CryptoCurrencyRepository extends JpaRepository<CryptoCurrency, String> {


    @Modifying
    @Query(value = "truncate table crypto_currencies", nativeQuery = true)
    @Transactional
    void truncateTable();

    CryptoCurrency findTopBySymbol(String symbol);


    List<CryptoCurrency> findByCodeIn(Set<String> code);


}
