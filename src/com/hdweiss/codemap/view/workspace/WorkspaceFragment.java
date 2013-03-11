package com.hdweiss.codemap.view.workspace;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.Utils;
import com.hdweiss.codemap.view.workspace.outline.OutlineBrowser;

public class WorkspaceFragment extends Fragment implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	public static final String WORKSPACE_NAME = "workspace_name";
	public static final String PROJECT_NAME = "project_name";
	private static final String ZOOM = "zoom";
	private static final String SCROLL_X = "scrollX";
	private static final String SCROLL_Y = "scrollY";
	
	private WorkspaceView codeMapView;
	private OutlineBrowser codeMapBrowser;
	private WorkspaceController controller;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);
				
		View view = inflater.inflate(R.layout.workspace, container, false);
		codeMapView = (WorkspaceView) view.findViewById(R.id.codemap);
		codeMapBrowser = (OutlineBrowser) view.findViewById(R.id.codemap_browser);

		init();
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		prefs.registerOnSharedPreferenceChangeListener(this);

		if(savedInstanceState != null)
			restoreInstanceState(savedInstanceState);
		
		return view;
	}
	
	private void init() {
		String projectName = getArguments().getString(PROJECT_NAME);
		String workspaceName = getArguments().getString(WORKSPACE_NAME);
		
		this.controller = new WorkspaceController(projectName, workspaceName, getActivity());
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
	public void onResume() {
		super.onResume();
		this.getActivity().registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		this.getActivity().unregisterReceiver(receiver);
		super.onPause();
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
	
	
	public static final String INTENT_REFRESH = "com.hdweiss.codemap.codebrowser.refresh";
	private IntentFilter filter = new IntentFilter(INTENT_REFRESH);
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			codeMapBrowser.refresh();
		}
	};
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("sourceFontSize")) {
			int fontSize = Utils.getSourceFontsize(getActivity());
			codeMapView.setFontSize(fontSize);
		}
	}

}