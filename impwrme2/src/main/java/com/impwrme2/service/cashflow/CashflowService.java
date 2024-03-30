package com.impwrme2.service.cashflow;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.repository.cashflow.CashflowRepository;

@Service
public class CashflowService {

	@Autowired
	private CashflowRepository cashflowRepository;

	@Transactional(readOnly = true)
	public Optional<Cashflow> findById(Long id) {
		return cashflowRepository.findById(id);
	}

	@Transactional
	public Cashflow save(Cashflow cashflow) {
		Cashflow savedCashflow = cashflowRepository.save(cashflow);
		return savedCashflow;
	}

	/**
	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
	 * why this delete appears here and not in the ResourceParamService.
	 * @param resourceParam The parameter to be deleted.
	 */
	@Transactional
	public void deleteCashflowDateRangeValue(CashflowDateRangeValue cashflowDateRangeValue) {
		Cashflow cashflow = cashflowDateRangeValue.getCashflow();
		cashflow.removeCashflowDateRangeValue(cashflowDateRangeValue);
		cashflowRepository.save(cashflow);	
	}
}
