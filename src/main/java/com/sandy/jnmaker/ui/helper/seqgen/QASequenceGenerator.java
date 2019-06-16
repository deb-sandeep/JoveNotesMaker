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
        else if( lastSeq.endsWith( "Hdr" ) ) {
            int questionNumber = Integer.parseInt( lastSeq.substring( 0, lastSeq.length()-3 ) ) ;
            return Integer.toString( questionNumber ) + ".1" ;
        }
        else if( lastSeq.contains( "." ) ) {
            int index = lastSeq.lastIndexOf( '.' ) ;
            int subQuestionIndex = Integer.parseInt( lastSeq.substring( index+1 ) ) ;
            return lastSeq.substring( 0, index ) + "." + Integer.toString( ++subQuestionIndex ) ;
        }
        else {
            int questionNumber = Integer.parseInt( lastSeq ) ;
            return questionNumber + "Ans" ;
        }
    }
}
