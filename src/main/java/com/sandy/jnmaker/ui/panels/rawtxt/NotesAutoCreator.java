package com.sandy.jnmaker.ui.panels.rawtxt;

import com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.*;

public class NotesAutoCreator {
    
    public static String autoCreateFIBNote( String input ) {
        return new FIBAutoCreator( input ).createNote() ;
    }
    
    public static String autoCreateDefinitionNote( String input ) {
        return new DefinitionAutoCreator( input ).createNote() ;
    }
    
    public static String autoCreateQANote( String input ) {
        return new QAAutoCreator( input ).createNote() ;
    }

    public static String autoCreateMatchingNote( String input ) {
        return new MatchingAutoCreator( input ).createNote() ;
    }

    public static String autoCreateMultiChoiceNote( String input ) {
        return new MultiChoiceAutoCreator( input ).createNote() ;
    }

    public static String autoCreateTrueFalseNote( String input ) {
        return new TrueFalseAutoCreator( input ).createNote() ;
    }
}
