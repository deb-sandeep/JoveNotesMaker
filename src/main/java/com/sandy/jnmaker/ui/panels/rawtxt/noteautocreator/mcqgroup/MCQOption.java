package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqgroup;

public class MCQOption {
    public String answer = null ;
    public boolean isCorrect = false ;
    
    public MCQOption( String ans, boolean isCorrect ) {
        this.answer = ans ;
        this.isCorrect = isCorrect ;
    }
    
    public MCQOption( String ans ) {
        this( ans, false ) ; 
    }
}
