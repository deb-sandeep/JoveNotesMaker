package com.sandy.jnmaker.ui.panels.search;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;
import static javax.swing.JOptionPane.showMessageDialog ;

import java.awt.event.ActionEvent ;
import java.io.File ;
import java.io.IOException ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.event.ListSelectionEvent ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.lucene.ChapterInfo ;
import com.sandy.jnmaker.lucene.NoteInfo ;
import com.sandy.jnmaker.lucene.search.Searcher ;

@SuppressWarnings( "serial" )
public class SearchInputPanel extends SearchInputPanelUI {
    
    static final Logger log = Logger.getLogger( SearchInputPanel.class ) ;
    
    private Searcher searcher = null ;
    
    public SearchInputPanel() throws Exception {
        super() ;
    }
    
    public void setQueryAndSearch( String searchString ) {
        super.queryTF.setText( searchString ) ;
        searchAndDisplayResults() ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        if( e.getSource() == super.searchBtn || 
            e.getSource() == super.queryTF ) {
            
            searchAndDisplayResults() ;
        }
        else if( e.getSource() == super.moveSelectedBtn ) {
            
            copySelectedResultsToNotesPane() ;
        }
    }
    
    private Searcher getSearcher() throws Exception {
        if( searcher == null ) {
            searcher = new Searcher() ;
        }
        return searcher ;
    }
    
    private void searchAndDisplayResults() {
        
        String queryStr = super.queryTF.getText() ;
        if( StringUtil.isEmptyOrNull( queryStr ) ) {
            return ;
        }
        
        List<NoteInfo> results = null ;
        
        try {
            results = getSearcher().search( queryStr, 100 ) ;
        }
        catch( Exception e1 ) {
            showMessageDialog( this, "Error in search\n" + e1.getMessage() ) ;
            return ;
        }
        super.tableModel.setSearchResults( results ) ;
    }
    
    @Override
    public void valueChanged( ListSelectionEvent e ) {

        if( !e.getValueIsAdjusting() ) {
            StringBuilder buffer = new StringBuilder() ;
            for( NoteInfo note : getSelectedNotes() ) {
                buffer.append( note.getContent() )
                      .append( "\n\n" ) ;
            }
            super.jnSrcPane.setText( buffer.toString() ) ;
        }
    }
    
    private void copySelectedResultsToNotesPane() {
        
        StringBuilder buffer = new StringBuilder() ;
        for( NoteInfo note : getSelectedNotes() ) {
            appendNoteSource( note, buffer ) ;
        }
        
        if( StringUtil.isNotEmptyOrNull( buffer.toString().trim() ) ) {
            getMainFrame().getJNPanel().addNote( buffer.toString() ) ;
        }
    }
    
    private void appendNoteSource( NoteInfo note, StringBuilder buffer ) {
        
        buffer.append( "// " )
              .append( note.getChapter().getSyllabus() )
              .append( ":" )
              .append( note.getChapter().getSubject() )
              .append( ":" ) 
              .append( note.getChapter().getChapterNum() )
              .append( "." )
              .append( note.getChapter().getSubChapterNum() )
              .append( " - " )
              .append( note.getChapter().getChapterName() ) 
              .append( "\n" ) ;
        
        buffer.append( note.getContent() )
              .append( "\n\n" ) ;
        
        if( !note.getMediaFiles().isEmpty() ) {
            copyMediaFiles( note ) ;
        }
    }
    
    private void copyMediaFiles( NoteInfo note ) {
        
        File       srcFile       = new File( note.getChapter().getSrcPath() ) ;
        File       srcBaseDir    = srcFile.getParentFile() ;
        List<File> srcMediaFiles = note.getMediaFiles() ;
        
        ChapterInfo currentJNFileInfo = getMainFrame().getJNPanel()
                                                      .getChapterInfo() ;
        
        if( currentJNFileInfo != null ) {
            File destBaseFolder = new File( currentJNFileInfo.getSrcPath() )
                                      .getParentFile() ;
            
            for( File srcMedia : srcMediaFiles ) {
                copyMediaFile( srcBaseDir, srcMedia, destBaseFolder ) ;
            }
        }
    }
    
    private void copyMediaFile( File srcBaseDir, File srcMedia, File destBaseFolder ) {
        
        String relPath = srcMedia.getAbsolutePath()
                                 .substring( srcBaseDir.getAbsolutePath()
                                                       .length()+1 ) ;
        
        File tgtFilePath = new File( destBaseFolder, relPath ) ;
        if( !tgtFilePath.getParentFile().exists() ) {
            tgtFilePath.mkdirs() ;
        }
        
        try {
            FileUtils.copyFile( srcMedia, tgtFilePath ) ;
        }
        catch( IOException e ) {
            log.error( "Unable to copy file.", e ) ;
        }
    }
    
    private List<NoteInfo> getSelectedNotes() {
        
        List<NoteInfo> selectedNotes = new ArrayList<NoteInfo>() ;
        int viewRows[] = resultsTable.getSelectedRows() ;
        for( int viewRow : viewRows ) {
            int modelRow = resultsTable.convertRowIndexToModel( viewRow ) ;
            
            NoteInfo result = tableModel.getResult( modelRow ) ;
            selectedNotes.add( result ) ;
        }
        return selectedNotes ;
    }
}
