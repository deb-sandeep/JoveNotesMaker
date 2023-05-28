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

        String aoiPattern = "_([^_]+)_" ;
        Pattern pattern = Pattern.compile( aoiPattern, Pattern.DOTALL ) ;
        Matcher matcher = pattern.matcher( input ) ;

        int stringMark = 0 ;

        String retVal = null ;
        String termStr = null ;
        String definitionStr = null ;

        if( matcher.find() ) {

            int matchStart = matcher.start() ;
            String matchString = matcher.group( 0 ) ;

            termStr = matcher.group( 1 ) ;
            stringMark = matchStart + matchString.length() ;
        }

        if( stringMark > 0 && stringMark < input.length() ) {
            definitionStr = input.subSequence( stringMark, input.length() )
                    .toString()
                    .trim() ;
        }

        if( termStr != null && definitionStr != null ) {
            retVal = "@definition \"" + escapeQuotes( termStr ) + "\"\n" ;
            retVal += "\"" + formatText( definitionStr, true ) + "\"\n" ;
            retVal += "\n" ;
        }

        return retVal ;
    }
}
