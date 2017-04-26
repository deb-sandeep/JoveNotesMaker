package com.sandy.jnmaker.indexer;

import static com.sandy.jnmaker.util.ObjectRepository.getAppConfig ;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.util.XTextModelParser ;

public class IndexingDaemon extends Thread {
    
    private static final Logger log = Logger.getLogger( IndexingDaemon.class ) ;

    private SourceProcessingJournal journal = null ;
    private List<File> sourceDirectories = new ArrayList<File>() ;
    private XTextModelParser modelParser = null ;
    
    public IndexingDaemon() {
        super( "JoveNotesMaker indexing daemon" ) ;
        super.setDaemon( true ) ;
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
    
    public void run() {
        
        try {
            initialize() ;
        }
        catch( Exception e ) {
            log.error( "Exception in initializing indexing daemon.", e ) ;
            return ;
        }
        
        List<File> files = null ;
        try {
            files = journal.getFilesForProcessing() ;
        }
        catch( Exception e ) {
            log.error( "Error getting files for processing.", e ) ;
            return ;
        }
        
        for( File file : files ) {
            if( file.exists() ) {
                try {
                    indexFile( file ) ;
                    journal.updateSuccessfulProcessingStatus( file ) ;
                }
                catch( Exception e ) {
                    log.debug( "Could not process file. " + file.getAbsolutePath(), e ) ;
                    journal.updateFailureProcessingStatus( file ) ;
                }
            }
            else {
                try {
                    removeFileFromIndex( file ) ;
                    journal.removeFileEntry( file ) ;
                }
                catch( Exception e ) {
                    log.debug( "Could not remove file from journal. " + 
                               file.getAbsolutePath(), e ) ;
                }
            }
        }
    }
    
    private void initialize() throws Exception {
        
        log.debug( "Initializing indexing daemon" ) ;
        File workspaceDir = getAppConfig().getWorkspaceDir() ;
        File journalFile  = new File( workspaceDir, "jnm-journal.txt" ) ;
        
        journal = new SourceProcessingJournal( journalFile, sourceDirectories ) ;
        log.debug( "Source processing journal loaded." ) ;
        
        modelParser = new XTextModelParser( "com.sandy.xtext.JoveNotesStandaloneSetup" ) ;
        log.debug( "Model parser loaded" ) ;
    }
    
    private void indexFile( File file ) throws Exception {
        
    }
    
    private void removeFileFromIndex( File file ) throws Exception {
        
    }

//
//private void parseJNFile( File file ) throws Exception {
//  
//  JoveNotes ast = ( JoveNotes )modelParser.parseFile( file ) ;
//  
//  for( NotesElement element : ast.getNotesElements() ) {
//      ICompositeNode cmpNode = NodeModelUtils.getNode( element ) ;
//      if( cmpNode != null ) {
//          String sourceText = cmpNode.getText() ;
//          logger.info( sourceText ) ;
//      }
//  }
//}
}
