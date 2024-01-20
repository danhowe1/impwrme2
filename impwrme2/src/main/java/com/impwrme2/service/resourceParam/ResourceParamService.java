package com.impwrme2.service.resourceParam;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.repository.resourceParam.ResourceParamRepository;

@Service
public class ResourceParamService {

	@Autowired
	private ResourceParamRepository resourceParamRepository;

	public ResourceParamService(ResourceParamRepository resourceParamRepository) {
		this.resourceParamRepository = resourceParamRepository;
	}

	@Transactional(readOnly = true)
	public Optional<ResourceParam<?>> findById(Long id) {
		return resourceParamRepository.findById(id);
	}

	@Transactional
	public ResourceParam<?> save(ResourceParam<?> resourceParam) {
// TODO May need this...		resourceParamValidator.validate(resourceParam);
		ResourceParam<?> savedResourceParam = resourceParamRepository.save(resourceParam);
		return savedResourceParam;
	}
}
