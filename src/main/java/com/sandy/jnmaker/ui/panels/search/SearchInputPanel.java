package com.sandy.jnmaker.ui.panels.search;

import static javax.swing.JOptionPane.showMessageDialog ;

import java.awt.event.ActionEvent ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.lucene.NoteInfo ;
import com.sandy.jnmaker.lucene.search.Searcher ;
import com.sandy.jnmaker.lucene.search.Searcher.SearchException ;

@SuppressWarnings( "serial" )
public class SearchInputPanel extends SearchInputPanelUI {
    
    static final Logger log = Logger.getLogger( SearchInputPanel.class ) ;
    
    private Searcher searcher = null ;
    
    public SearchInputPanel() throws Exception {
        super() ;
        searcher = new Searcher() ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        String queryStr = super.queryTF.getText() ;
        if( StringUtil.isEmptyOrNull( queryStr ) ) {
            return ;
        }
        
        List<NoteInfo> results = null ;
        
        try {
            results = searcher.search( queryStr, 100 ) ;
        }
        catch( SearchException e1 ) {
            showMessageDialog( this, "Error in query\n" + e1.getMessage() ) ;
            return ;
        }
        
        processResults( results ) ;
    }
    
    private void processResults( List<NoteInfo> results ) {
        for( NoteInfo info : results ) {
            log.debug( info.getContent() ) ;
        }
    }
}
