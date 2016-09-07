package com.sandy.jnmaker.ui.helper.seqgen;

public class NumericIncrementSequenceGenerator extends SequenceGenerator {

    public NumericIncrementSequenceGenerator( String prefix, String[] seqParts ) {
        super( prefix, seqParts ) ;
    }

    @Override
    protected String generateNewLastSeqPart() {
        String lastSeq = super.getLastSeqPart() ;
        int    intVal  = Integer.parseInt( lastSeq ) ;
        return Integer.toString( ++intVal ) ;
    }
}
