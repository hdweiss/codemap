package com.hdweiss.codemap.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;

public class CodeMapFragment extends Fragment {

	CodeMapView codeMapView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.codemap, container, false);
		codeMapView = (CodeMapView) view.findViewById(R.id.codemap);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.codemap, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.codemap_clear:
			codeMapView.clear();
			return true;
			
		case R.id.codemap_resetZoom:
			codeMapView.setZoom(1, new CodeMapPoint(0,0));
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
