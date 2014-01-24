package com.innovention.weddingplanner.bean;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateMandatory;
import static com.google.common.base.Preconditions.*;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

public class Task implements IDtoBean {

	private final static String TAG = Task.class.getSimpleName();

	private int id;
	private String description;
	
	/**
	 * Builder class for Task bean
	 * @author YCH
	 *
	 */
	public static class Builder {
		
		private int id;
		private String description;
		
		public Builder() {
			
		}
		
		public Builder withId(final int id) {
			checkArgument(id > 0, "Id should be greater than 0");
			this.id = id;
			return this;
		}
		
		public Builder withDesc(final String desc) {
			this.description = desc;
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
		this.description = builder.description;
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
	public void validate() throws MissingMandatoryFieldException {
		validateMandatory(getDescription());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).omitNullValues().add("id", getId())
				.add("description", getDescription()).toString();
	}

}
