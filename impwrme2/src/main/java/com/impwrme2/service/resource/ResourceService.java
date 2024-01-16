package com.impwrme2.service.resource;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.repository.resource.ResourceRepository;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	public ResourceService(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Resource> findById(Long id) {
		return resourceRepository.findById(id);
	}

	@Transactional
	public Resource save(Resource resource) {
// TODO May need this...		resourceValidator.validate(resource);
		Resource savedResource = resourceRepository.save(resource);
		return savedResource;
	}

//	/**
//	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
//	 * why this delete appears here and not in the ResourceParamService.
//	 * @param resourceParam The parameter to be deleted.
//	 */
//	@Transactional
//	public void deleteResourceParam(ResourceParam<?> resourceParam) {
//		Resource resource = resourceParam.getResource();
//		resource.removeResourceParam(resourceParam);
//		resourceRepository.save(resource);	
//	}
}
