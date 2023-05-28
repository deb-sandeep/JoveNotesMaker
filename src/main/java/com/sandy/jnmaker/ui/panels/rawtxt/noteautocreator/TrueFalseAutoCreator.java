package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import static com.sandy.jnmaker.util.NoteTextUtil.escapeQuotes;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class TrueFalseAutoCreator {

    private final String input ;

    public TrueFalseAutoCreator( String input ) {
        this.input = input ;
    }

    public String createNote() {

        String[] lines = input.split( "\\R" ) ;

        String retVal = "@true_false \"" + escapeQuotes( lines[0] ) + "\"\n" ;

        if( lines.length == 1 ) {
            retVal += "true\n";
        }
        else {
            retVal += "false\n";
            retVal += "\"" + formatText( lines[1], true ) + "\"\n";
        }
        retVal += "\n" ;

        return retVal ;
    }
}
