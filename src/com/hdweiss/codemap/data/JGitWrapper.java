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

import android.os.AsyncTask;

public class JGitWrapper {

	private String localPath;
	private String remotePath;
	
	private Git git;
	
	public JGitWrapper(String localPath, String remoteUri) throws IOException {
		this.localPath = localPath;
		this.remotePath = remoteUri;
		
		FileRepository localRepo = new FileRepository(localPath + "/.git");
		git = new Git(localRepo);
	}
	
    public void cloneRepo() {
    	GitInfo info = new GitInfo(this.localPath, this.remotePath);
    	new CloneRepoTask().execute(info);
    }
    
    private class GitInfo {
    	public String localPath;
    	public String remotePath;
    	
    	public GitInfo(String localPath, String remotePath) {
    		this.localPath = localPath;
    		this.remotePath = remotePath;
    	}
    }
    
    private class CloneRepoTask extends AsyncTask<GitInfo, Integer, Long> {
		@Override
		protected Long doInBackground(GitInfo... params) {
			GitInfo gitInfo = params[0];
			
	        try {
				Git.cloneRepository().setURI(gitInfo.remotePath)
						.setDirectory(new File(gitInfo.localPath)).call();
			} catch (InvalidRemoteException e) {
				e.printStackTrace();
			} catch (TransportException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}  
			
			return 0L;
		}
    	
    }
    
    public void pull() {
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
