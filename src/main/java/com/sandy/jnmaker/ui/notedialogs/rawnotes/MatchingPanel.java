package com.sandy.jnmaker.ui.notedialogs.rawnotes ;

import static com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator.autoCreateMatchingNote ;

import com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class MatchingPanel extends RawNotesPanelUI {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( MatchingPanel.class ) ;

    public MatchingPanel( String input ) {
        
        UIUtil.associateEditMenu( rawNotesTF ) ;
        setUpListeners() ;
        rawNotesTF.setText( autoCreateMatchingNote( input ) ) ;
    }
    
    private void setUpListeners() {
        super.bindOkPressEventCapture( this.rawNotesTF ) ;
    }
    
    protected void captureFocus() {
        rawNotesTF.requestFocus() ;
    }

    @Override
    public String getFormattedNote() {
        return rawNotesTF.getText().trim() ;
    }
}
