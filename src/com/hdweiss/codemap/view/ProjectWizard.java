package com.hdweiss.codemap.view;

import java.io.IOException;
import java.util.ArrayList;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.util.Utils;

public class ProjectWizard extends DialogFragment {

	private Project project;	
	
	private EditText nameView;
	private EditText urlView;
	private Spinner syncSpinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.project_wizard, container);
		
		this.nameView = (EditText) view.findViewById(R.id.wizard_name);
		this.urlView = (EditText) view.findViewById(R.id.wizard_url);
		this.syncSpinner = (Spinner) view.findViewById(R.id.wizard_synchronizer);
		
		Button okButton = (Button) view.findViewById(R.id.wizard_ok);
		okButton.setOnClickListener(okClick);
		
		Button cancelButton = (Button) view.findViewById(R.id.wizard_cancel);
		cancelButton.setOnClickListener(cancelClick);
		
		setup();
		return view;
	}

	private void setup() {
		if(project != null) {
			nameView.setText(project.getName());
			urlView.setText(project.getUrl());
			getDialog().setTitle("Edit project");
		} else
			getDialog().setTitle("Add project");
		
		ArrayList<String> syncSources = new ArrayList<String>();
		syncSources.add("git");
		Utils.setupSpinner(syncSpinner, syncSources, "git");
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public Project saveProject() {
		final String name = nameView.getText().toString();
		final String url = urlView.getText().toString();
		
		Project project = new Project(name);
		project.setUrl(url);
		
		try {
			project.writeProject(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "Error writing project",
					Toast.LENGTH_LONG).show();
		}
		return project;
	}
	
	private OnClickListener okClick = new OnClickListener() {
		public void onClick(View v) {
			saveProject();
			ProjectBrowser browser = (ProjectBrowser) getActivity();
			browser.refresh();
			dismiss();
		}
	};
	
	private OnClickListener cancelClick = new OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};
}
