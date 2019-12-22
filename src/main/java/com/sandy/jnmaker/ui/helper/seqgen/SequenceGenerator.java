package com.sandy.jnmaker.ui.helper.seqgen;

public interface SequenceGenerator {

    public String getNextSequence() ;
    
    public void rollbackSequence() ;

    public boolean isMatchingSequence( String sequence ) ;
}
