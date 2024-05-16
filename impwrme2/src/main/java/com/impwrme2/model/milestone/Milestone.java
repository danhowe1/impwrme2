package com.impwrme2.model.milestone;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.resource.Resource;

public class Milestone {

	public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");

	private final Resource resource;

	private final YearMonth yearMonth;

	private final MilestoneType milestoneType;

	private final List<String> messageArgs = new ArrayList<String>();

	public Milestone(Resource resource, YearMonth yearMonth, MilestoneType milestoneType, String... messageArgs) {
		this.resource = resource;
		this.yearMonth = yearMonth;
		this.milestoneType = milestoneType;
		for (String arg : messageArgs) {
	        this.messageArgs.add(arg);
	    }
	}
	
	// --------------------
	// Getters and Setters.
	// --------------------
	
	public Resource getResource() {
		return resource;
	}

	public YearMonth getYearMonth() {
		return yearMonth;
	}

	public MilestoneType getMilestoneType() {
		return milestoneType;
	}

	public String[] getMessageArgs() {
		return messageArgs.toArray(new String[messageArgs.size()]);
	}
}
