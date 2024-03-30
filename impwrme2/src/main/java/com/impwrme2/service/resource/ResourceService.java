package com.impwrme2.service.resource;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.repository.resource.ResourceRepository;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Transactional(readOnly = true)
	public Optional<Resource> findById(Long id) {
		return resourceRepository.findById(id);
	}

	@Transactional
	public Resource save(Resource resource) {
		Resource savedResource = resourceRepository.save(resource);
		return savedResource;
	}

	/**
	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
	 * why this delete appears here and not in the ResourceService.
	 * @param resource The resource to be deleted.
	 */
	@Transactional
	public void deleteCashflow(Cashflow cashflow) {
		Resource resource = cashflow.getResource();
		resource.removeCashflow(cashflow);
		@SuppressWarnings("unused")
		Resource savedResource = resourceRepository.save(resource);
	}
}
