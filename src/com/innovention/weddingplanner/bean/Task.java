package com.innovention.weddingplanner.bean;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateMandatory;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.getStringResource;
import static com.innovention.weddingplanner.Constantes.*;
import static com.google.common.base.Preconditions.*;

import org.joda.time.DateTime;

import android.content.Context;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.innovention.weddingplanner.R;
import com.innovention.weddingplanner.exception.InconsistentFieldException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

public class Task implements IDtoBean {

	private final static String TAG = Task.class.getSimpleName();

	private int id;
	private Boolean active = Boolean.TRUE;
	private String description;
	private DateTime dueDate;
	private DateTime remindDate;
	private String remindChoice;
	
	/**
	 * Builder class for Task bean
	 * @author YCH
	 *
	 */
	public static class Builder {
		
		private int id;
		private Boolean active = Boolean.TRUE;
		private String description;
		private DateTime dueDate;
		private DateTime remindDate;
		private String remindChoice;
		
		public Builder() {
			
		}
		
		public Builder withId(final int id) {
			this.id = id;
			return this;
		}
		
		public Builder setActive(final Boolean statut) {
			checkNotNull(statut, "Statut of the task can not be null");
			this.active = statut;
			return this;
		}
		
		public Builder withDesc(final String desc) {
			this.description = desc;
			return this;
		}
		
		public Builder dueDate(DateTime due) {
			this.dueDate = due;
			return this;
		}
		
		public Builder remind(DateTime reminder) {
			this.remindDate = reminder;
			return this;
		}
		
		public Builder remindOption(String option) {
			this.remindChoice = option;
			return this;
		}
		
		public Task build() {
			return new Task(this);
		}
	}

	private Task() {
	}
	
	private Task(final Builder builder) {
		this.id = builder.id;
		this.active = builder.active;
		this.description = builder.description;
		this.dueDate = builder.dueDate;
		this.remindDate = builder.remindDate;
		this.remindChoice = builder.remindChoice;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void validate(Context ctx) throws MissingMandatoryFieldException, InconsistentFieldException {
		checkNotNull(ctx, "Context can not be null");
		validateMandatory(getDescription());
		// Enforce consistency between due date date and reminder
		if ( null == getDueDate() && !getRemindChoice().equals(getStringResource(ctx, R.string.task_spinner_item1)) ) {
			throw new InconsistentFieldException(INCONSISTENT_FIELD_EX);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).omitNullValues().add("id", getId())
				.add("description", getDescription())
				.add("dueDate", getDueDate())
				.add("remindDate", getRemindDate())
				.add("reminder option", getRemindChoice())
				.add("active", getActive())
				.toString();
	}

	public DateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(DateTime dueDate) {
		this.dueDate = dueDate;
	}

	public DateTime getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(DateTime remindDate) {
		this.remindDate = remindDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getRemindChoice() {
		return remindChoice;
	}

	public void setRemindChoice(String remindChoice) {
		this.remindChoice = remindChoice;
	}

}
