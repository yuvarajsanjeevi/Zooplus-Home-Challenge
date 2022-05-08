package com.cryptocurrency.service.repository;

import com.cryptocurrency.service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yuvaraj.sanjeevi
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
