package com.sandy.jnmaker.ui.helper.seqgen;

public class JEETestQuestionSequenceGenerator extends SequenceGenerator {

    private String[] parts = null ;
    
    public JEETestQuestionSequenceGenerator( String[] parts ) {
        super() ;
        this.parts = parts ;
    }

    @Override
    public String getNextSequence() {
        parts[parts.length-1] = generateNewLastSeqPart() ;
        return collateParts() ;
    }

    @Override
    public boolean isMatchingSequence( String sequence ) {
        return collateParts().equals( sequence ) ;
    }

    @Override
    protected String generateNewLastSeqPart() {
        String lastSeq = parts[parts.length-1] ;
        
        if( lastSeq.contains( "." ) ) {
            String[] seqParts = lastSeq.split( "\\." ) ;
            Integer lastSeqInt = parseInteger( seqParts[seqParts.length-1] ) ;
            if( lastSeqInt == null ) {
                return lastSeq ;
            }
            else {
                StringBuffer buffer = new StringBuffer() ;
                for( int i=0; i<seqParts.length-1; i++ ) {
                    buffer.append( seqParts[i] ).append( "." ) ;
                }
                buffer.append( ++lastSeqInt ) ;
                return buffer.toString() ;
            }
        }
        else {
            Integer lastSeqInt = parseInteger( lastSeq ) ;
            if( lastSeqInt == null ) {
                return lastSeq ;
            }
            else {
                return String.valueOf( ++lastSeqInt ) ;
            }
        }
    }
    
    private Integer parseInteger( String text ) {
        try {
            return Integer.parseInt( text ) ;
        }
        catch( Exception e ) {
            return null ;
        }
    }
    
    private String collateParts() {
        StringBuilder buffer = new StringBuilder() ;
        for( int i=0; i<parts.length; i++ ) {
            buffer.append( parts[i] ).append( "_" ) ;
        }
        buffer.deleteCharAt( buffer.length()-1 ) ;
        return buffer.toString() ;
    }
}
