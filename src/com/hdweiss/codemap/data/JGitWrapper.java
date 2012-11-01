package com.hdweiss.codemap.data;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hdweiss.codemap.view.ProjectItemView;

public class JGitWrapper {
	
	private Project project;
	private Git git;
	private Context context;
	
	
	private ProjectController controller;
	
	public JGitWrapper(Project project, Context context) throws IOException {
		this.project = project;
		this.context = context;
		FileRepository localRepo = new FileRepository(project.getSourcePath(context) + "/.git");
		git = new Git(localRepo);
	}
	
	public void update(ProjectController controller, ProjectItemView itemView) {
		this.controller = controller;
		File sourceRepo = new File(project.getSourcePath(context));

    	GitInfo info = new GitInfo(project);
		
		if(sourceRepo.exists()) {
	    	new PullRepoTask(itemView).execute(info);
		}
	    else
	    	new CloneRepoTask(itemView).execute(info);
	}

    
    private class CloneRepoTask extends AsyncTask<GitInfo, Integer, Long> {

    	private ProjectItemView itemView;
    	private String status = "";
    	
		public CloneRepoTask(ProjectItemView itemView) {
			this.itemView = itemView;
			itemView.startUpdate();
		}

		@Override
		protected Long doInBackground(GitInfo... params) {
			GitInfo gitInfo = params[0];
			
	        try {
				Git.cloneRepository().setURI(gitInfo.remotePath)
						.setDirectory(new File(gitInfo.localPath)).setProgressMonitor(new ProgressMonitor() {
							
							public void update(int arg0) {
								publishProgress(arg0);
							}
							
							public void start(int arg0) {
								publishProgress(arg0);						
							}
							
							public boolean isCancelled() {
								return false;
							}
							
							public void endTask() {						
							}
							
							public void beginTask(String arg0, int arg1) {
								status = arg0;
								publishProgress(arg1);	
							}
						}).call();
								
			} catch (Exception e) {
				status = e.getLocalizedMessage();
				publishProgress(100);
			}
			
			return 0L;
		}
		

		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			itemView.setProgress(values[0]);
			itemView.setStatus(status);
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			controller.buildIndex();
			Toast.makeText(context, "Done updating", Toast.LENGTH_SHORT).show();
		}
    }
    
    private class PullRepoTask extends AsyncTask<GitInfo, Integer, Long> {
    	
		private ProjectItemView itemView;
		private String status = "";

		public PullRepoTask(ProjectItemView itemView) {
			this.itemView = itemView;
			itemView.startUpdate();
		}

		@Override
		protected Long doInBackground(GitInfo... params) {
			try {
				git.pull().setProgressMonitor(new ProgressMonitor() {
					
					public void update(int arg0) {
						publishProgress(arg0);
					}
					
					public void start(int arg0) {
						publishProgress(arg0);						
					}
					
					public boolean isCancelled() {
						return false;
					}
					
					public void endTask() {						
					}
					
					public void beginTask(String arg0, int arg1) {
						status = arg0;
						publishProgress(arg1);	
					}
				}).call();
			} catch (Exception e) {
				status = e.getLocalizedMessage();
				publishProgress(100);
			}
			return 0L;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			itemView.setProgress(values[0]);
			itemView.setStatus(status);
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			controller.buildIndex();
			Toast.makeText(context, "Done updating", Toast.LENGTH_SHORT).show();
		}
    }
    
    private class GitInfo {
    	public String localPath;
    	public String remotePath;
    	
    	public GitInfo(Project project) {
    		this.localPath = project.getSourcePath(context);
    		this.remotePath = project.getUrl();
    	}
    }
}
