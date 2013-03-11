package com.hdweiss.codemap.view.workspace;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hdweiss.codemap.data.CscopeEntry;

public class FindDeclarationTask extends AsyncTask<Object, Object, Object>
{
	private ProgressDialog dialog;

	private Context context;
	private FindDeclarationCallback callback;
	private String url;
	private WorkspaceController controller;

	private ArrayList<CscopeEntry> entries;


	
	public FindDeclarationTask(String url, FindDeclarationCallback callback,
			WorkspaceController controller, Context context) {
		this.url = url;
		this.callback = callback;
		this.controller = controller;
		this.context = context;
	}
	
	@Override
	protected Object doInBackground(final Object... urls) {
		try {
			this.entries = controller.getUrlEntries(url);
		} catch (IllegalArgumentException e) {
			Log.e("CodeMap",
					"Error getting entries for url: " + url + " "
							+ e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Object result) {
		super.onPostExecute(result);
		hideDialog();
		if (this.entries != null)
			callback.onSuccess(entries);
		else
			callback.onFailure();
	}

    @Override
    protected void onPreExecute()
    {
    	super.onPreExecute();
    	showDialog();
    }
    
    private void showDialog() {
    	this.dialog = new ProgressDialog(context);
    	dialog.setMessage("Finding declarations for: \"" + url + "\"");
    	dialog.setIndeterminate(false);
    	dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	dialog.show();
    }
    
    private void hideDialog() {
    	dialog.dismiss();
    }
    
    public interface FindDeclarationCallback {
    	public void onSuccess(ArrayList<CscopeEntry> entries);
    	public void onFailure();
    }
}
