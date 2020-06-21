package com.sandy.jnmaker.ui.helper.seqgen;

import org.apache.commons.lang.ArrayUtils ;

public class JEEQuestion {
    
    public String sub = null ;
    public String paperId = null ;
    public String qType = null ;
    public float  qId = 0.0F ;
    public int    qGroupNumber = 0 ;
    public int    qNumber = 0 ;
    
    public JEEQuestion( String fileName ) {
        
        String fName = fileName ;
        
        if( fName.endsWith( ".png" ) ) {
            fileName.substring( 0, fileName.length()-4 ) ;
        }
        
        if( fName.contains( "(" ) ) {
            fName = fName.substring( 0, fName.indexOf( '(' ) ) ;
        }
        
        String[] parts = fName.split( "_" ) ;
        int qTypePartIndex = getQTypePartIndex( parts ) ;
        
        sub = parts[0] ;
        qType = parts[qTypePartIndex] ;
        
        
        String[] paperIdParts = (String[])ArrayUtils.subarray( parts, 2, qTypePartIndex ) ; 
        paperId = String.join( "_", paperIdParts ) ;

        String qIdStr = "" ;
        for( int i=qTypePartIndex+1; i<parts.length; i++ ) {
            qIdStr += parts[i] ;
            if( i < parts.length-1 ) {
                qGroupNumber = Integer.parseInt( parts[i] ) ;
                qIdStr += "." ;
            }
            
            if( i == parts.length-1 ) {
                qNumber = Integer.parseInt( parts[i] ) ;
            }
        }
        qId = Float.parseFloat( qIdStr ) ;
    }
    
    public boolean isPartOfLCT() {
        return qType.equals( "LCT" ) ;
    }
    
    public boolean isLCTParagraph() {
        return qType.equals( "LCT" ) && ( qId == (int)qId ) ;
    }
    
    public boolean isLCTQuestion() {
        return qType.equals( "LCT" ) && ( qId != (int)qId ) ;
    }
    
    private int getQTypePartIndex( String[] parts ) {
        for( int i=2; i<parts.length; i++ ) {
            String part = parts[i] ;
            if( part.equals( "SCA" ) || 
                part.equals( "MCA" ) || 
                part.equals( "MMT" ) || 
                part.equals( "NT" )  || 
                part.equals( "CMT" )  || 
                part.equals( "LCT" ) ) {
                return i ;
            }
        }
        throw new RuntimeException( "No qtype found in " + String.join( "_", parts ) ) ;
    }
    
    public String getFileName() {
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( sub ).append( "_Q_" )
              .append( paperId ).append( "_" )
              .append( qType ) ;
        
        if( qGroupNumber > 0 ) {
            buffer.append( "_" + qGroupNumber ) ;
        }
        
        if( qNumber > 0 ) {
            buffer.append( "_" + qNumber ) ;
        }
        
        return buffer.toString() ;
    }
    
    public static void main( String[] args ) {
        JEEQuestion q = new JEEQuestion( "Phy_Q_AL03_F1_LCT_1_23.png" ) ;
        q.qNumber += 1 ;
        q.qGroupNumber = -1 ;
        q.qType = "MCA" ;
        System.out.println( q.getFileName() ) ;
    }
}


