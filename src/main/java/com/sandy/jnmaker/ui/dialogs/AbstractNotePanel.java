package com.sandy.jnmaker.ui.dialogs;

import javax.swing.JPanel ;

public abstract class AbstractNotePanel extends JPanel {

    private static final long serialVersionUID = -2589969430412971534L ;
    
    protected NotesCreatorDialog parent = null ;
    
    public abstract String getFormattedNote() ;

    public void setParentDialog( NotesCreatorDialog parent ) {
        this.parent = parent ;
    }
    
    protected String escapeQuotes( String input ) {
        return input.replaceAll( "\"", "\\\"" ) ;
    }
}
