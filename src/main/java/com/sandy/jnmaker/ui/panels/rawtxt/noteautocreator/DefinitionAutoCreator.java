package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sandy.jnmaker.util.NoteTextUtil.escapeQuotes;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class DefinitionAutoCreator {

    private final String input ;

    public DefinitionAutoCreator( String input ) {
        this.input = input ;
    }

    public String createNote() {
        
        String[] parts = input.split( "\\R", 2 ) ;
        
        String retVal = null ;
        String termStr = null ;
        String definitionStr = null ;
        
        if( parts.length == 2 ) {
            termStr       = parts[0].trim();
            definitionStr = parts[1].trim();
        }
        
        if( termStr != null ) {
            retVal = "@definition \"" + termStr + "\"\n" ;
            retVal += "\"" + formatText( definitionStr, true ) + "\"\n" ;
            retVal += "\n" ;
        }

        return retVal ;
    }
}
