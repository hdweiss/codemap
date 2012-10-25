package com.hdweiss.codemap.data;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
	
	public void update(ProjectController controller) {
		this.controller = controller;
		File sourceRepo = new File(project.getSourcePath(context));
		
		if(sourceRepo.exists())
			pull();
		else
			cloneRepo();
	}
	
    private void cloneRepo() {    	
    	GitInfo info = new GitInfo(project);
    	new CloneRepoTask().execute(info);
    }
    
    private class GitInfo {
    	public String localPath;
    	public String remotePath;
    	
    	public GitInfo(Project project) {
    		this.localPath = project.getSourcePath(context);
    		this.remotePath = project.getUrl();
    	}
    }
    
    private class CloneRepoTask extends AsyncTask<GitInfo, Integer, Long> {
		@Override
		protected Long doInBackground(GitInfo... params) {
			GitInfo gitInfo = params[0];
			
	        try {
				Git.cloneRepository().setURI(gitInfo.remotePath)
						.setDirectory(new File(gitInfo.localPath)).call();
				
				int i = 2;
				int count = 10;
				
				publishProgress((int) ((i / (float) count) * 100));				
			} catch (InvalidRemoteException e) {
				e.printStackTrace();
			} catch (TransportException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
			
			return 0L;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			int progress = values[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			controller.buildIndex();
			Toast.makeText(context, "Done updating", Toast.LENGTH_SHORT).show();
			
		}
    }
    
    private void pull() {
    	GitInfo info = new GitInfo(project);
    	new PullRepoTask().execute(info);
    }
    
    private class PullRepoTask extends AsyncTask<GitInfo, Integer, Long> {
		@Override
		protected Long doInBackground(GitInfo... params) {
			pull();

			int i = 2;
			int count = 10;

			publishProgress((int) ((i / (float) count) * 100));

			return 0L;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			int progress = values[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			controller.buildIndex();
			Toast.makeText(context, "Done updating", Toast.LENGTH_SHORT).show();
			
		}

		private void pull() {
			try {
				git.pull().call();
			} catch (WrongRepositoryStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DetachedHeadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RefNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoHeadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
}
