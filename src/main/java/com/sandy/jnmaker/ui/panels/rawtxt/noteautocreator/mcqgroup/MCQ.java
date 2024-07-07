package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MCQ {

    private int numOptionsPerRow = 1 ;
    private String question = null ;
    private List<MCQOption> options = new ArrayList<>() ;
    
    public MCQ( String question ) {
        this( question, 3 ) ;
    }
    
    public MCQ( String question, int numOptionsPerRow ) {
        this.question = question ;
        this.numOptionsPerRow = numOptionsPerRow ;
    }
    
    public void addOption( MCQOption option ) {
        this.options.add( option ) ;
        Collections.shuffle( this.options ) ;
    }
    
    public int getNumOptions() {
        return this.options.size() ;
    }
    
    public boolean containsOption( String optionString ) {
        for( MCQOption option : options ) {
            if( option.answer.equals( optionString ) ) 
                return true ;
        }
        return false ;
    }
    
    public String toString() {

        StringBuilder sb = new StringBuilder() ;
        sb.append( "@multi_choice \"" + question + "\" {\n" )
          .append( "    @options {\n" ) ;
        
        for( MCQOption option : options ) {
            sb.append( "       \"" + option.answer + "\"" ) ;
            if( option.isCorrect ) {
                sb.append( " correct" ) ;
            }
            sb.append( ",\n" ) ;
        }
        
        sb.deleteCharAt( sb.length()-1 ) ;
        sb.deleteCharAt( sb.length()-1 ) ;
        sb.append( "\n    }" ) ;
        sb.append( "\n    @numOptionsPerRow " + this.numOptionsPerRow ) ;
        sb.append( "\n}" ) ;
        return sb.toString() ;
    }
}
