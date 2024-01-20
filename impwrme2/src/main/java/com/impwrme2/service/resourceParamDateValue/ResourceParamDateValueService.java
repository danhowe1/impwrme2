package com.impwrme2.service.resourceParamDateValue;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.repository.resourceParamDateValue.ResourceParamDateValueRepository;

@Service
public class ResourceParamDateValueService {

	@Autowired
	private ResourceParamDateValueRepository resourceParamDateValueRepository;

	public ResourceParamDateValueService(ResourceParamDateValueRepository resourceParamDateValueRepository) {
		this.resourceParamDateValueRepository = resourceParamDateValueRepository;
	}

	@Transactional(readOnly = true)
	public Optional<ResourceParamDateValue<?>> findById(Long id) {
		return resourceParamDateValueRepository.findById(id);
	}

	@Transactional
	public ResourceParamDateValue<?> save(ResourceParamDateValue<?> resourceParamDateValue) {
// TODO May need this...		resourceParamDateValueValidator.validate(resourceParamDateValue);
		ResourceParamDateValue<?> savedResourceParamDateValue = resourceParamDateValueRepository.save(resourceParamDateValue);
		return savedResourceParamDateValue;
	}
}
