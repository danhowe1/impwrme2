package com.impwrme2.service.cashflowDateRangeValue;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.repository.cashflowDateRangeValue.CashflowDateRangeValueRepository;

@Service
public class CashflowDateRangeValueService {

	@Autowired
	private CashflowDateRangeValueRepository cashflowDateRangeValueRepository;

	@Transactional(readOnly = true)
	public Optional<CashflowDateRangeValue> findById(Long id) {
		return cashflowDateRangeValueRepository.findById(id);
	}

	@Transactional
	public CashflowDateRangeValue save(CashflowDateRangeValue cashflowDateRangeValue) {
		CashflowDateRangeValue savedCashflowDateRangeValue = cashflowDateRangeValueRepository.save(cashflowDateRangeValue);
		return savedCashflowDateRangeValue;
	}
}
