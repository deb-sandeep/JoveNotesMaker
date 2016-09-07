package com.sandy.jnmaker.ui.helper.seqgen;

public class QASequenceGenerator extends SequenceGenerator {

    public QASequenceGenerator( String prefix, String[] seqParts ) {
        super( prefix, seqParts ) ;
    }

    @Override
    protected String generateNewLastSeqPart() {
        String lastSeq = super.getLastSeqPart() ;
        if( lastSeq.endsWith( "Ans" ) ) {
            int questionNumber = Integer.parseInt( lastSeq.substring( 0, lastSeq.length()-3 ) ) ;
            return Integer.toString( ++questionNumber ) ;
        }
        else {
            int questionNumber = Integer.parseInt( lastSeq ) ;
            return questionNumber + "Ans" ;
        }
    }
}
