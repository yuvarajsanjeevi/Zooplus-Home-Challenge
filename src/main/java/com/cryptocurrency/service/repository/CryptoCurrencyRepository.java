package com.cryptocurrency.service.repository;

import com.cryptocurrency.service.entity.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yuvaraj.sanjeevi
 */
@Repository
public interface CryptoCurrencyRepository extends JpaRepository<CryptoCurrency, String> {


    @Modifying
    @Query(value = "truncate table crypto_currencies", nativeQuery = true)
    @Transactional
    void truncateTable();

}
