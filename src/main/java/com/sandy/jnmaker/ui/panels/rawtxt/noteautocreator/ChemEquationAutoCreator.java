package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import com.sandy.common.util.StringUtil;
import com.sandy.jnmaker.util.NoteTextUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class ChemEquationAutoCreator {

    private static final Logger log = Logger.getLogger( ChemEquationAutoCreator.class ) ;

    private final String input ;

    public ChemEquationAutoCreator(String input ) {
        this.input = input ;
    }

    public String createNote() {

        int firstArrowIndex = input.indexOf( '>' ) ;
        int lastArrowIndex  = input.lastIndexOf( '>' ) ;

        String reactants = input.substring( 0, firstArrowIndex ).trim() ;
        String produces  = input.substring( firstArrowIndex+1, lastArrowIndex ).trim() ;
        String products  = input.substring( lastArrowIndex+1 ).trim() ;

        if( StringUtil.isEmptyOrNull( produces ) ) {
            produces = "" ;
        }
        else {
            produces = " \"" + NoteTextUtil.escapeSlash( produces ) + "\" " ;
        }

        return "@chem_equation {\n" +
               "    \"" + reactants + "\" >" + produces + "> \"" + products + "\"\n" +
               "}\n\n" ;
    }
}
