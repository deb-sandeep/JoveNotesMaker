package com.sandy.jnmaker.util;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

public class AppConfig {

    private File joveNotesMediaDir = null ;
    private List<File> sourceDirectories = new ArrayList<File>() ;

    public File getJoveNotesMediaDir() {
        return joveNotesMediaDir;
    }

    public void setJoveNotesMediaDir( File joveNotesMediaDir ) {
        this.joveNotesMediaDir = joveNotesMediaDir;
    }
    
    public List<File> getJNSrcDirs() {
        return sourceDirectories ;
    }
    
    public void setSourceDirectories( String paths ) {
        
        String[] dirs = paths.split( ":" ) ;
        for( String dir : dirs ) {
            File file = new File( dir ) ;
            if( !file.exists() ) {
                throw new IllegalArgumentException(
                        "The directory " + dir + " does not exist." ) ;
            }
            else if( !file.isDirectory() ) {
                throw new IllegalArgumentException(
                        "The directory " + dir + " is not a directory." ) ;
            }
            else {
                sourceDirectories.add( file ) ;
            }
        }
    }
}
