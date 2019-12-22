package com.sandy.jnmaker.ui.helper.seqgen;

public abstract class AbstractSequenceGenerator implements SequenceGenerator {

    private String   prefix = null ;
    private String[] seqParts = null ;
    
    private String prevLastSeqPart = null ;
    
    protected AbstractSequenceGenerator() {
        // To be used by those subclasses who don't want the default
        // sequence generation behavior
    }
    
    protected AbstractSequenceGenerator( String prefix, String[] seqParts ) {
        this.prefix = prefix ;
        this.seqParts = seqParts ;
    }
    
    public String getNextSequence() {
        
        prevLastSeqPart = seqParts[ seqParts.length-1 ] ;
        
        String newLastSeqPart = generateNewLastSeqPart() ;
        seqParts[ seqParts.length-1 ] = newLastSeqPart ;
        return constructSequence() ;
    }
    
    public void rollbackSequence() {
        seqParts[ seqParts.length-1 ] = prevLastSeqPart ;
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
