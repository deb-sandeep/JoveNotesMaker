package com.sandy.jnmaker.lucene.indexer;

import static com.sandy.jnmaker.lucene.indexer.LuceneHelper.removeComments ;
import static com.sandy.jnmaker.util.ObjectRepository.getAppConfig ;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.eclipse.xtext.nodemodel.ICompositeNode ;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils ;

import com.sandy.jnmaker.lucene.ChapterInfo ;
import com.sandy.jnmaker.lucene.NoteInfo ;
import com.sandy.jnmaker.util.XTextModelParser ;
import com.sandy.xtext.joveNotes.ChapterDetails ;
import com.sandy.xtext.joveNotes.JoveNotes ;
import com.sandy.xtext.joveNotes.NotesElement ;

public class IndexingDaemon extends Thread {
    
    private static final Logger log = Logger.getLogger( IndexingDaemon.class ) ;

    private SourceProcessingJournal journal = null ;
    private List<File> sourceDirectories = new ArrayList<File>() ;
    private XTextModelParser modelParser = null ;
    private Indexer indexer = null ;
    
    private Thread daemonThread = null ;
    private boolean keepRunning = true ;
    
    public IndexingDaemon() {
        super( "JoveNotesMaker indexing daemon" ) ;
        super.setDaemon( true ) ;
    }
    
    public void initialize() throws Exception {
        
        log.debug( "Initializing indexing daemon" ) ;
        File workspaceDir = getAppConfig().getWorkspaceDir() ;
        File journalFile  = new File( workspaceDir, "jnm-journal.txt" ) ;
        
        journal = new SourceProcessingJournal( journalFile, sourceDirectories ) ;
        log.debug( "Source processing journal loaded." ) ;
        
        modelParser = new XTextModelParser() ;
        log.debug( "Model parser loaded" ) ;
        
        indexer = new Indexer() ;
        indexer.initialize() ;
        log.debug( "Indexer initialized." ) ;
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
    
    public void shutDown() {
        this.daemonThread.interrupt() ;
    }
    
    public void run() {
        
        while( keepRunning ) {
            this.daemonThread = this ;
            try {
                indexFiles() ;
                Thread.sleep( 5*60000 ) ;
            }
            catch( InterruptedException e ) {
                log.debug( "Daemon is interrupted." ) ;
                keepRunning = false ;
            }
            catch( Exception e ) {
                log.error( "Daemon suffered an error", e ) ;
                keepRunning = false ;
            }
        }

        try {
            indexer.close() ;
        }
        catch( Exception e ) {
            log.error( "Error closing indexer", e ) ;
        }
    }
    
    private void indexFiles() throws Exception {
        
        List<File> files = null ;
        try {
            files = journal.getFilesForProcessing() ;
        }
        catch( Exception e ) {
            log.error( "Error getting files for processing.", e ) ;
            return ;
        }
        
        for( File file : files ) {
            if( !this.keepRunning ) return ;
            if( file.exists() ) {
                try {
                    File baseDir = journal.getSourceDirForFile( file ) ;
                    // We can encounter cases where a file is found which doesn't 
                    // belong // to any of the source directories specified. 
                    // This is a valid scenario and can happen in the following 
                    // cases:
                    // 
                    // 1. The journal had indexed a file in the past, and the source
                    //    directory list was changed recently excluding the earlier 
                    //    base directories.
                    //
                    // If such a case happens, we are in a soup - we can't
                    // determine the syllabus and hence can't index the file.
                    // We can't let the index entries be because we don't know
                    // how the file has changed - it's better to purge it
                    // from the search index.
                    if( baseDir == null ) {
                        removeFileFromIndex( file ) ;
                        journal.removeFileEntry( file ) ;
                    }
                    else {
                        indexFile( file ) ;
                        journal.updateSuccessfulProcessingStatus( file ) ;
                    }
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
            Thread.yield() ;
        }
    }
    
    private void indexFile( File file ) throws Exception {
        
        log.debug( "Indexing = " + file.getAbsolutePath() ) ;
        // If we are trying to index a file, it can be either because the 
        // file has changed or it is a new file. In case it is a new file, 
        // we will add new documents to the index. However, if the file has
        // changed, we run the risk of adding duplicate documents if we don't
        // detect the collison and overlap. Detecting changes at a note level
        // is a costly operation, instead we just remove the chapter from the
        // index and reindex the whole file again.
        
        removeFileFromIndex( file ) ;
        
        JoveNotes      ast        = (JoveNotes) modelParser.parseFile( file ) ;
        ChapterDetails chpDetails = ast.getChapterDetails() ;
        ChapterInfo    ci         = getChapterInfo( file, chpDetails ) ;
        
        for( NotesElement element : ast.getNotesElements() ) {
            
            if( !this.keepRunning ) {
                return ;
            }
            
            ICompositeNode cmpNode = NodeModelUtils.getNode( element ) ;
            if( cmpNode != null ) {
                
                String sourceText = removeComments( cmpNode.getText() ).trim() ;
                NoteInfo ni = NoteInfoBuilder.build( ci, element, 
                                                     sourceText, file ) ;
                if( ni != null ) {
                    indexer.add( ni ) ;
                }
            }
        }
    }
    
    public ChapterInfo getChapterInfo( File file ) 
        throws Exception {
        
        JoveNotes      ast        = (JoveNotes) modelParser.parseFile( file ) ;
        ChapterDetails chpDetails = ast.getChapterDetails() ;
        ChapterInfo    ci         = getChapterInfo( file, chpDetails ) ;
        
        return ci ;
    }
    
    private ChapterInfo getChapterInfo( File file, ChapterDetails cd ) {
        
        ChapterInfo ci = null ;

        ci = new ChapterInfo()
            .setSrcPath( file.getAbsolutePath() )
            .setSyllabus( getSyllabusName( file ) )
            .setSubject( cd.getSubjectName() )
            .setChapterNum( cd.getChapterNumber() )
            .setSubChapterNum( cd.getSubChapterNumber() )
            .setChapterName( cd.getChapterName() ) ;
        
        return ci ;
    }
    
    private void removeFileFromIndex( File file ) throws Exception {
        indexer.deleteDocumentsFromSource( file ) ;
    }
    
    private String getSyllabusName( File file ) {
        
        File baseDir = journal.getSourceDirForFile( file ) ;
        
        // This should not happen normally as all the rootless files should
        // have been handled earlier.
        if( baseDir == null ) {
            throw new IllegalStateException( "Found a file which is not in " + 
                                             "specified source directories." ) ;
        }
        
        String srcDirPath = baseDir.getAbsolutePath() ;
        String filePath   = file.getAbsolutePath() ;
        String relPath    = filePath.substring( srcDirPath.length() + 1 ) ;
        
        String syllabus = relPath.substring( 0, relPath.indexOf( File.separatorChar ) ) ;
        return syllabus ;
    }
}
