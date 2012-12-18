package com.hdweiss.codemap.data;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hdweiss.codemap.controller.ProjectController;
import com.hdweiss.codemap.util.Utils;

public class JGitWrapper {
	
	private Context context;
	private Git git;
	
	private Project project;
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
		
    	GitCommand<?> command;
    	
		if(sourceRepo.exists()) {
			command = git.pull();
		}
	    else {
	    	command = Git.cloneRepository().setURI(project.getUrl())
			.setDirectory(new File(project.getSourcePath(context)));
	    }
		
		new GitTask(project.getName()).execute(command);
	}

    
    private class GitTask extends AsyncTask<GitCommand<?>, Integer, Long> {
    	
		private String projectName;
		private String status;

		public GitTask(String projectName) {
			this.projectName = projectName;
		}

		@Override
		protected Long doInBackground(GitCommand<?>... params) {
			GitCommand<?> command = params[0];
			
			Utils.announceSyncStart(context, projectName);

			if(command instanceof CloneCommand) {
				((CloneCommand)command).setProgressMonitor(monitor);
				status = "Cloning";
			} else if(command instanceof PullCommand) {
				((PullCommand)command).setProgressMonitor(monitor);
				status = "Pulling";
			} else {
				throw new IllegalArgumentException(
						"Coudln't attach progressMonitor to git command");
			}
			
			publishProgress(-1);
			
			try {
				command.call();
			} catch (Exception e) {
				status = e.getLocalizedMessage();
				publishProgress(100);
			}
			return 0L;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			
			Utils.announceSyncUpdateProgress(context, projectName, progress, status);
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			controller.buildIndex();
			Toast.makeText(context, "Updated " + project.getName(), Toast.LENGTH_SHORT).show();
			Utils.announceSyncDone(context, projectName);
		}
		
		private ProgressMonitor monitor = new ProgressMonitor() {

			private int progress = 0;
			private int totalWork = 0;
			private int workCompleted = 0;

			public void start(int totalTasks) {
				Log.d("CodeMap", "total tasks: " + totalTasks);
			}

			public void beginTask(String title, int totalWork) {
				Log.d("CodeMap", "starting: " + title + " : " + totalWork);

				this.totalWork = totalWork;
				this.workCompleted = 0;
				status = title;
				publishProgress(0);
			}

			public void update(int completed) {
				this.workCompleted += completed;
				//Log.d("CodeMap", "completed: " + workCompleted + "/" + totalWork);
				int newProgress = getProgress();
				
				if(this.progress != newProgress) {
					this.progress = newProgress;
					publishProgress(newProgress);
				}
			}

			private int getProgress() {
				if(totalWork == 0)
					return 0;
				
				final int taskWorkProgress = (int) ((100.0 / totalWork)
						* workCompleted);
				return taskWorkProgress;
			}

			public void endTask() {
			}

			public boolean isCancelled() {
				return false;
			}
		};
    }
}
