package com.sandy.jnmaker.ui.helper.seqgen;

public class Sequencer {

    public static class LCharIncrementSequenceGenerator extends AbstractSequenceGenerator {

        public LCharIncrementSequenceGenerator( String prefix, String[] seqParts ) {
            super( prefix, seqParts ) ;
        }

        @Override protected String generateNewLastSeqPart() {
            
            String lastSeq  = super.getLastSeqPart() ;
            char   lastChar = lastSeq.charAt( lastSeq.length()-1 ) ;
            return lastSeq.substring( 0, lastSeq.length()-1 ) + (++lastChar) ;
        }
    }
    
    public static class QASequenceGenerator extends AbstractSequenceGenerator {

        public QASequenceGenerator( String prefix, String[] seqParts ) {
            super( prefix, seqParts ) ;
        }

        @Override protected String generateNewLastSeqPart() {
            
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
    
    public static class NumericIncrementSequenceGenerator extends AbstractSequenceGenerator {

        public NumericIncrementSequenceGenerator( String prefix, String[] seqParts ) {
            super( prefix, seqParts ) ;
        }

        @Override protected String generateNewLastSeqPart() {
            
            String lastSeq = super.getLastSeqPart() ;
            int    intVal  = Integer.parseInt( lastSeq ) ;
            return Integer.toString( ++intVal ) ;
        }
    }
    
    public static class NoActionSequenceGenerator extends AbstractSequenceGenerator {

        public NoActionSequenceGenerator( String prefix, String[] seqParts ) {
            super( prefix, seqParts ) ;
        }

        @Override protected String generateNewLastSeqPart() {
            return super.getLastSeqPart() ;
        }
    }
    
    public static SequenceGenerator identifySequence( String inputSequence ) {
        
        SequenceGenerator seqGen = null ;
        String[] parts = inputSequence.split( "_" ) ;
        
        if( parts.length <= 1 ) {
            return new NoActionSequenceGenerator( parts[0], parts ) ;
        }
        
        String   prefix   = parts[0].trim() ;
        String[] seqParts = parts[1].trim().split( "\\." ) ;
        
        String lastSeqPart = seqParts[seqParts.length-1] ;
        
        if( lastSeqPart.matches( "[0-9]+" ) ) {
            seqGen = new NumericIncrementSequenceGenerator( prefix, seqParts ) ;
        }
        else if( lastSeqPart.matches( "[0-9]+Ans" ) ) {
            seqGen = new QASequenceGenerator( prefix, seqParts ) ;
        }
        else if( lastSeqPart.matches( "[0-9]+Hdr" ) ) {
            seqGen = new QASequenceGenerator( prefix, seqParts ) ;
        }
        else if( lastSeqPart.matches( "[0-9]+[A-Z]" ) ||
                 lastSeqPart.matches( "[0-9]+[a-z]" ) ) {
            seqGen = new LCharIncrementSequenceGenerator( prefix, seqParts ) ;
        }
        else if( prefix.equals( "Phy" ) ||
                 prefix.equals( "Chem" ) || 
                 prefix.equals( "Math" ) ) {
            seqGen = new JEETestQuestionSequenceGenerator( parts ) ;
        }
        else {
            seqGen = new NoActionSequenceGenerator( prefix, seqParts ) ;
        }
        
        return seqGen ;
    }
}
