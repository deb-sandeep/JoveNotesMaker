package com.sandy.jnmaker.ui.helper.seqgen;

public class Sequencer {

    public static SequenceGenerator identifySequence( String inputSequence ) {
        SequenceGenerator seqGen = null ;
        
        String[] parts    = inputSequence.split( "_" ) ;
        String   prefix   = parts[0].trim() ;
        String[] seqParts = parts[1].trim().split( "\\." ) ;
        
        String lastSeqPart = seqParts[seqParts.length-1] ;
        
        if( lastSeqPart.matches( "[0-9]+" ) ) {
            seqGen = new NumericIncrementSequenceGenerator( prefix, seqParts ) ;
        }
        else if( lastSeqPart.matches( "[0-9]+Ans" ) ) {
            seqGen = new QASequenceGenerator( prefix, seqParts ) ;
        }
        else if( lastSeqPart.matches( "[0-9]+[A-Z]" ) ||
                 lastSeqPart.matches( "[0-9]+[a-z]" ) ) {
            seqGen = new LCharIncrementSequenceGenerator( prefix, seqParts ) ;
        }
        else {
            seqGen = new NoActionSequenceGenerator( prefix, seqParts ) ;
        }
        
        return seqGen ;
    }
}
