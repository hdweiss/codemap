package com.hdweiss.codemap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CodeMapFragment extends Fragment {

	private CodeMapView codeMapView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.codemap, container, false);
		codeMapView = (CodeMapView) view.findViewById(R.id.codemap);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	//codeMapView.getThread().run();
	}
	
	
}
