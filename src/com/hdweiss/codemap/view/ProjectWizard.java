package com.hdweiss.codemap.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.Project;

public class ProjectWizard extends DialogFragment {

	private EditText nameView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.project_wizard, container);
		
		this.nameView = (EditText) view.findViewById(R.id.wizard_name);
		
		Button okButton = (Button) view.findViewById(R.id.wizard_ok);
		okButton.setOnClickListener(okClick);
		
		Button cancelButton = (Button) view.findViewById(R.id.wizard_cancel);
		cancelButton.setOnClickListener(cancelClick);

		return view;
	}

	public Project getProject() {
		final String name = nameView.getText().toString();
		Project project = new Project(name, "");
		return project;
	}
	
	private OnClickListener okClick = new OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};
	
	private OnClickListener cancelClick = new OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};
}
