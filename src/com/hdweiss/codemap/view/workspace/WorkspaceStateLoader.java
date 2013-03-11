package com.hdweiss.codemap.view.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.data.SerializableLink;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapLink;

public class WorkspaceStateLoader extends AsyncTask<ArrayList<SerializableItem>, CodeMapItem, Long> {
	
	private WorkspaceState state;
	private WorkspaceView codeMapView;
	private WorkspaceController controller;

	private ProgressDialog dialog;
	private HashMap<UUID, CodeMapItem> codeMapItems = new HashMap<UUID, CodeMapItem>();
	
	public WorkspaceStateLoader(WorkspaceState state, WorkspaceView codeMapView, WorkspaceController controller) {
		this.state = state;
		this.codeMapView = codeMapView;
		this.controller = controller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		this.dialog = ProgressDialog.show(codeMapView.getContext(), "Loading",
				"Loading state...");
	}

	protected Long doInBackground(ArrayList<SerializableItem>... objects) {
		ArrayList<SerializableItem> items = objects[0];
		
		for (int i = 0; i < items.size(); i++) {
			CodeMapItem fragment = loadObjectState(items.get(i));
			this.publishProgress(fragment);
			codeMapItems.put(fragment.id, fragment);
		}

		return (long) 0;
	}

	@Override
	protected void onProgressUpdate(CodeMapItem... progress) {
		super.onProgressUpdate(progress);
		for(int i = 0; i < progress.length; i++)
			codeMapView.addMapItem(progress[i]);
	}

	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		loadLinksState(state);
		dialog.dismiss();
	}
	

	private CodeMapItem loadObjectState(SerializableItem item) {
		CodeMapItem itemView = item.createCodeMapItem(controller,
				codeMapView.getContext());
		itemView.id = item.id;
		return itemView;
	}
	
	private void loadLinksState(WorkspaceState state) {
		for (SerializableLink link : state.links) {
			CodeMapItem parent = codeMapItems.get(link.parent);
			CodeMapItem child = codeMapItems.get(link.child);

			codeMapView.addMapLink(new CodeMapLink(parent, child,
					link.offset));
		}
	}
}
