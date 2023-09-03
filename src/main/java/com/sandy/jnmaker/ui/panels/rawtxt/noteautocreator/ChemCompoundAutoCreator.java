package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import org.apache.log4j.Logger;

import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class ChemCompoundAutoCreator {

    private static final Logger log = Logger.getLogger( ChemCompoundAutoCreator.class ) ;

    private final String input ;

    public ChemCompoundAutoCreator(String input ) {
        this.input = input ;
    }

    public String createNote() {

        String[] parts = input.split( "\\R", 3 ) ;

        String formula      = null ;
        String chemicalName = null ;
        String commonName   = "" ;

        if( parts.length >= 2 ) {
            formula      = parts[0];
            chemicalName = parts[1];
        }

        if( parts.length == 3 ) {
            commonName = parts[2];
        }

        return "@chem_compound \"" + formula.trim() + "\"\n" +
               "\"" + formatText( chemicalName.trim() ) + "\"\n" +
               "\"" + formatText( commonName.trim() ).trim() + "\"\n" +
               "\n";
    }

    public static void main(String[] args) {
        String input =  "NaCl\n" +
                        "Sodium Chloride\n" ;
        System.out.println( new ChemCompoundAutoCreator( input ).createNote() ) ;
    }
}
