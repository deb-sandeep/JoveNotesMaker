package com.sandy.jnmaker.ui.panels.search;

import java.util.ArrayList ;
import java.util.List ;

import javax.swing.table.AbstractTableModel ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.lucene.NoteInfo ;

@SuppressWarnings( "serial" )
public class SearchResultsTableModel extends AbstractTableModel {

    static final Logger logger = Logger.getLogger( SearchResultsTableModel.class ) ;

    public static final Object[][] COL_PROPERTIES = {
        { "Syllabus",    String.class },
        { "Subject",     String.class },
        { "#",           String.class },
        { "Chapter Name",String.class },
        { "Note Type",   String.class },
        { "Content",     String.class }
    } ;

    public static final int COL_SYLLABUS     = 0 ;
    public static final int COL_SUBJECT      = 1 ;
    public static final int COL_CHAPTER_NO   = 2 ;
    public static final int COL_CHAPTER_NAME = 3 ;
    public static final int COL_NOTE_TYPE    = 4 ;
    public static final int COL_CONTENT      = 5 ;

    private List<NoteInfo> results = new ArrayList<NoteInfo>() ; 

    @Override
    public int getColumnCount() { return COL_PROPERTIES.length ; }

    @Override 
    public int getRowCount() {
        return results.size() ;
    }

    @Override
    public String getColumnName( int column ) {
        return ( String )COL_PROPERTIES[column][0] ;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return false ;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        
        Object val = null ;
        NoteInfo result = results.get( rowIndex ) ;
        
        switch( columnIndex ) {
            case COL_SYLLABUS :
                val = result.getChapter().getSyllabus() ;
                break ;
                
            case COL_SUBJECT :
                val = result.getChapter().getSubject() ;
                break ;
                
            case COL_CHAPTER_NO :
                val = result.getChapter().getChapterNum() + "." +
                      result.getChapter().getSubChapterNum() ;
                break ;
                
            case COL_CHAPTER_NAME :
                val = result.getChapter().getChapterName() ;
                break ;
                
            case COL_NOTE_TYPE :
                val = result.getType() ;
                break ;
                
            case COL_CONTENT :
                val = result.getContent() ;
                break ;
        }
        return val ;
    }

    @Override
    public Class<?> getColumnClass( final int columnIndex ) {
        return ( Class<?> )COL_PROPERTIES[columnIndex][1] ;
    }
    
    public void setSearchResults( List<NoteInfo> results ) {
        this.results = results ;
        super.fireTableDataChanged() ;
    }
    
    public NoteInfo getResult( int index ) {
        return this.results.get( index ) ;
    }
}
