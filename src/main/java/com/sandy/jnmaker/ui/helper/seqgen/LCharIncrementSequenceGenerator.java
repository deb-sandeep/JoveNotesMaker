package com.sandy.jnmaker.ui.helper.seqgen;

public class LCharIncrementSequenceGenerator extends SequenceGenerator {

    public LCharIncrementSequenceGenerator( String prefix, String[] seqParts ) {
        super( prefix, seqParts ) ;
    }

    @Override
    protected String generateNewLastSeqPart() {
        String lastSeq  = super.getLastSeqPart() ;
        char   lastChar = lastSeq.charAt( lastSeq.length()-1 ) ;
        return lastSeq.substring( 0, lastSeq.length()-1 ) + (++lastChar) ;
    }
}
