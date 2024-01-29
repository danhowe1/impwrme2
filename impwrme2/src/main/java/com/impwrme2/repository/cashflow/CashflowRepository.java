package com.impwrme2.repository.cashflow;

import org.springframework.data.jpa.repository.JpaRepository;

import com.impwrme2.model.cashflow.Cashflow;

public interface CashflowRepository extends JpaRepository<Cashflow, Long> {

}
