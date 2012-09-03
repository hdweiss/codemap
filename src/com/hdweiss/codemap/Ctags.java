package com.hdweiss.codemap;

public class Ctags {

    private native int test();
    private native int runmain();

    public Ctags() {
    	testLib();
    }
    
    public int testLib() {
    	return test();
    }
    
    public int runMain() {
    	return runmain();
    }
    
    static {
    	System.loadLibrary("ctags");
    } 
}
