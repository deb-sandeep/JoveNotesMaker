package com.sandy.jnmaker.util;

import java.io.File ;

public class AppConfig {

    private File joveNotesMediaDir = null ;
    private File workspaceDir = null ;
    
    public File getJoveNotesMediaDir() {
        return joveNotesMediaDir;
    }

    public void setJoveNotesMediaDir( File joveNotesMediaDir ) {
        this.joveNotesMediaDir = joveNotesMediaDir;
    }

    public File getWorkspaceDir() {
        return workspaceDir ;
    }

    public void setWorkspaceDir( File workspaeDir ) {
        this.workspaceDir = workspaeDir ;
    }
}
