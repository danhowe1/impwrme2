package com.impwrme2.controller.dto.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ResourceDto {

	private Long id;
	
	@NotEmpty(message = "{msg.validation.resource.name.notEmpty}")
	private String name;

	private String parentName;
	
	@NotEmpty(message = "{msg.validation.resource.startDate.notEmpty}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Start date must be in format MM YYYY")
	private String startYearMonth;

	@NotEmpty(message = "{msg.validation.resource.resourceType.notEmpty}")
	private String resourceType;

	private boolean userCanDelete = true;
	
	public List<String> cashflowCategoriesUsersCanCreate = new ArrayList<String>();

	public List<String> resourceParamNamesUsersCanCreate = new ArrayList<String>();

	//-------------------
	// Getters & setters.
	//-------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getStartYearMonth() {
		return startYearMonth;
	}

	public void setStartYearMonth(String startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public boolean isUserCanDelete() {
		return userCanDelete;
	}

	public void setUserCanDelete(boolean userCanDelete) {
		this.userCanDelete = userCanDelete;
	}

	public void addCashflowCategoryUsersCanCreate(String cashflowCategorysUsersCanCreate) {
		this.cashflowCategoriesUsersCanCreate.add(cashflowCategorysUsersCanCreate);
	}

	public List<String> getCashflowCategoriesUsersCanCreate() {
		return cashflowCategoriesUsersCanCreate;
	}

	public void addResourceParamNameUsersCanCreate(String resourceParamNameUsersCanCreate) {
		this.resourceParamNamesUsersCanCreate.add(resourceParamNameUsersCanCreate);
	}

	public List<String> getResourceParamNamesUsersCanCreate() {
		return resourceParamNamesUsersCanCreate;
	}
}
