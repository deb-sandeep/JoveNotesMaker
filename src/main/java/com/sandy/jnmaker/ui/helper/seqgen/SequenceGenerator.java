package com.sandy.jnmaker.ui.helper.seqgen;

public abstract class SequenceGenerator {

    private String   prefix = null ;
    private String[] seqParts = null ;
    
    protected SequenceGenerator( String prefix, String[] seqParts ) {
        this.prefix = prefix ;
        this.seqParts = seqParts ;
    }
    
    public final String getNextSequence() {
        
        String newLastSeqPart = generateNewLastSeqPart() ;
        seqParts[ seqParts.length-1 ] = newLastSeqPart ;
        return constructSequence() ;
    }
    
    private String constructSequence() {
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( prefix )
              .append( "_" ) ;
        
        for( String part : seqParts ) {
            buffer.append( part )
                  .append( "." ) ;
        }
        // Remove the last .
        buffer.deleteCharAt( buffer.length()-1 ) ;
        
        return buffer.toString() ;
    }
    
    public boolean isMatchingSequence( String sequence ) {
        return sequence.equals( constructSequence() ) ;
    }
    
    protected String getLastSeqPart() {
        return seqParts[seqParts.length-1] ;
    }
    
    protected abstract String generateNewLastSeqPart() ;
}
