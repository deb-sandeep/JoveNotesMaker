package com.sandy.jnmaker.ui.notedialogs.rawnotes ;

import static com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator.autoCreateMultiChoiceNote;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.helper.UIUtil ;

public class MultiChoicePanel extends RawNotesPanelUI {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( MultiChoicePanel.class ) ;
    
    public MultiChoicePanel( String input ) {
        
        UIUtil.associateEditMenu( rawNotesTF ) ;
        setUpListeners() ;
        rawNotesTF.setText( autoCreateMultiChoiceNote( input ) ) ;
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
