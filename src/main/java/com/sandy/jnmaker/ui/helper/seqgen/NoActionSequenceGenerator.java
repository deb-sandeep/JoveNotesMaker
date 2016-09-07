package com.sandy.jnmaker.ui.helper.seqgen;

public class NoActionSequenceGenerator extends SequenceGenerator {

    public NoActionSequenceGenerator( String prefix, String[] seqParts ) {
        super( prefix, seqParts ) ;
    }

    @Override
    protected String generateNewLastSeqPart() {
        return super.getLastSeqPart() ;
    }
}
