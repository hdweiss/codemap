package com.hdweiss.codemap.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import android.test.AndroidTestCase;

public class JGitTest extends AndroidTestCase {

    private final String localPath = "/sdcard/git-test";
    private final String remotePath = "git://github.com/hdweiss/test.git";
    
    private Repository localRepo;
    private Git git;

    public JGitTest() throws IOException {
    	super();
    	
    	new File(localPath).delete();
    	new File(localPath + ".git").delete();
    	
    	localRepo = new FileRepository(localPath + "/.git");
    	git = new Git(localRepo);        
    }
    
    public void testCreate() throws IOException {
        Repository newRepo = new FileRepository(localPath + ".git");
        newRepo.create();
    }

    public void testClone() throws IOException, InvalidRemoteException, TransportException, GitAPIException { 
        Git.cloneRepository() 
           .setURI(remotePath)
           .setDirectory(new File(localPath))
           .call();  
    }

    
    public void testAdd() throws IOException, GitAPIException { 
        File myfile = new File(localPath + "/myfile");
        myfile.createNewFile();
        git.add()
           .addFilepattern("myfile")
           .call();
    }

    public void testCommit() throws IOException, JGitInternalException, UnmergedPathsException, GitAPIException {
        git.commit()
           .setMessage("Added myfile")
           .call();
    }

    public void testPush() throws IOException, JGitInternalException, TransportException, GitAPIException {     
        git.push()
        	.call();
    }    

    public void testTrackMaster() throws IOException, JGitInternalException, GitAPIException {     
        git.branchCreate() 
           .setName("master")
           .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
           .setStartPoint("origin/master")
           .setForce(true)
           .call();
    }

    public void testPull() throws IOException, TransportException, GitAPIException {
        git.pull()
           .call();
    }
}
