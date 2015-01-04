package com.innovention.weddingplanner.bean;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.Constantes.INCONSISTENT_FIELD_EX;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.getStringResource;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateMandatory;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.innovention.weddingplanner.R;
import com.innovention.weddingplanner.exception.InconsistentFieldException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

public class Task implements IDtoBean, Parcelable {

	private final static String TAG = Task.class.getSimpleName();
	public static final String KEY_VALUE = "task_content";

	private int id;
	private Boolean active = Boolean.TRUE;
	private String description;
	private DateTime dueDate;
	private DateTime remindDate;
	private String remindChoice;
	private Period period = Period.CUSTOM;
	
	public enum Period {
		OVER_YEAR((byte) 1), FOURTH_QUARTER((byte) 2), THIRD_QUARTER((byte) 3), SECOND_QUARTER((byte) 4), FIRST_QUARTER((byte) 5), 
		OVER_WEEK((byte) 6), BEFORE_WEEK((byte) 7), DDAY((byte) 8), CUSTOM((byte) 9);
		
		private byte order;
		
		private Period(final byte order) {
			this.order = order;
		}
		
		public byte getOrder() {
			return order;
		}
	}
	
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
		private Period period = Period.CUSTOM;
		
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
		
		public Builder setPlanning(Period period) {
			this.period = period;
			return this;
		}
		
		public Task build() {
			return new Task(this);
		}
	}
	
	/**
	 * Builder for Parcelable feature
	 */
	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {

		@Override
		public Task createFromParcel(Parcel source) {
			return new Task(source);
		}

		@Override
		public Task[] newArray(int size) {
			return new Task[size];
		}
		
	};

	private Task() {
	}
	
	/**
	 * Special constructor for Parcelable implementation
	 * @param in
	 */
	private Task(Parcel in) {
		this.id = in.readInt();
		this.active = (Boolean) in.readValue(null);
		this.description = in.readString();
		this.dueDate = DateTime.parse(in.readString());
		this.remindDate = DateTime.parse(in.readString());
		this.remindChoice = in.readString();
		this.period = Period.valueOf(in.readString());
	}
	
	private Task(final Builder builder) {
		this.id = builder.id;
		this.active = builder.active;
		this.description = builder.description;
		this.dueDate = builder.dueDate;
		this.remindDate = builder.remindDate;
		this.remindChoice = builder.remindChoice;
		this.period = builder.period;
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
				.add("period", getPeriod())
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

	/**
	 * Implements parcelable interface
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Implements parcelable interface
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeValue(active);
		dest.writeString(description);
		dest.writeString(dueDate.toString());
		dest.writeString(remindDate.toString());
		dest.writeString(remindChoice);
		dest.writeString(period.name());
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

}
