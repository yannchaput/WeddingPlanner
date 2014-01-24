package com.innovention.weddingplanner;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaskFragment extends Fragment {
	
	static final String TAG = TaskFragment.class.getSimpleName();

	public TaskFragment() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Factory method
	 * @return an instance of TaskFragment
	 */
	public static TaskFragment newInstance() {
		TaskFragment fgt = new TaskFragment();
		return fgt;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_task, container, false);
	}

}
