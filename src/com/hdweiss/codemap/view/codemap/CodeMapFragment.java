package com.hdweiss.codemap.view.codemap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.util.CodeMapPoint;

public class CodeMapFragment extends Fragment {
	private static final String ZOOM = "zoom";
	private static final String SCROLL_X = "scrollX";
	private static final String SCROLL_Y = "scrollY";
	
	private CodeMapView codeMapView;
	private CodeMapBrowser codeMapBrowser;
	private ProjectController controller;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.codemap_view, container, false);
		codeMapView = (CodeMapView) view.findViewById(R.id.codemap);
		codeMapBrowser = (CodeMapBrowser) view.findViewById(R.id.codemap_browser);
				
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null)
			restoreInstanceState(savedInstanceState);
		
		this.controller = ((CodeMapActivity) getActivity()).getController();
		codeMapBrowser.setController(controller);
		controller.setView(codeMapView);
	}
	
	public void restoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			float zoom = savedInstanceState.getFloat(ZOOM);
			int scrollX = savedInstanceState.getInt(SCROLL_X);
			int scrollY = savedInstanceState.getInt(SCROLL_Y);

			codeMapView.setScaleFactor(zoom, new CodeMapPoint(0, 0));
			codeMapView.setScrollX(scrollX);
			codeMapView.setScrollY(scrollY);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putFloat(ZOOM, codeMapView.getScaleFactor());
		outState.putInt(SCROLL_X, codeMapView.getScrollX());
		outState.putInt(SCROLL_Y, codeMapView.getScrollY());
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
			codeMapView.setScaleFactor(1, new CodeMapPoint(0,0));
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public void onDestroy() {
		controller.saveCodeMapState();
		super.onDestroy();
	}
}
