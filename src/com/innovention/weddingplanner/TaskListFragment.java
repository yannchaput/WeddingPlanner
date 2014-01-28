package com.innovention.weddingplanner;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TaskListFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	static final String ARG_SECTION_NUMBER = "section_number";

	public TaskListFragment() {
		// TODO Auto-generated constructor stub
	}

	public static TaskListFragment newInstance() {
		return new TaskListFragment();
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View rootView = inflater.inflate(R.layout.fragment_task_dummy,
//				container, false);
//
//		// Necessary to set the menu visible for fragment
//		setHasOptionsMenu(true);
//
//		if (null != getArguments()) {
//			TextView dummyTextView = (TextView) rootView
//					.findViewById(R.id.section_label);
//			dummyTextView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
//		}
//		return rootView;
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_task_dummy,
				container, false);

		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);
		
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task, menu);
	}
}
