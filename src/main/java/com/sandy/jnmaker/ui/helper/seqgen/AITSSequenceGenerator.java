package com.sandy.jnmaker.ui.helper.seqgen;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

public class AITSSequenceGenerator implements SequenceGenerator {
    
    static final Logger log = Logger.getLogger( AITSSequenceGenerator.class ) ;
    
    private List<String> fileNames = new ArrayList<>() ;
    private int nextIndex = 0 ;
    
    public AITSSequenceGenerator( String input ) throws Exception {
        parseInput( input ) ;
    }
    
    private void parseInput( String input ) {
        String paperId = null ;
        String lines[] = input.split("\\r?\\n") ;
        
        List<String> phyFileNames  = new ArrayList<>() ;
        List<String> chemFileNames = new ArrayList<>() ;
        List<String> mathFileNames = new ArrayList<>() ;
        
        int sequence = 1 ;
        
        for( int i=0; i<lines.length; i++ ) {
            String line = lines[i].trim() ;
            if( i==0 ) {
                paperId = line ;
            }
            else {
                String[] parts = line.split( "\\s+" ) ;
                int numQ = Integer.parseInt( parts[0].trim() ) ;
                String sectionId = parts[1].trim() ;
                
                List<String> fileSuffixes = generateSuffixes( sequence, numQ, sectionId ) ;
                for( String suffix : fileSuffixes ) {
                    phyFileNames.add( "Phy_Q_" + paperId + "_" + suffix ) ;
                    chemFileNames.add( "Chem_Q_" + paperId + "_" + suffix ) ;
                    mathFileNames.add( "Math_Q_" + paperId + "_" + suffix ) ;
                }
                
                if( sectionId.startsWith( "LCT" ) ) {
                    sequence += fileSuffixes.size() - 1 ;;
                }
                else {
                    sequence += fileSuffixes.size() ;
                }
            }
        }
        
        fileNames.addAll( phyFileNames ) ;
        fileNames.addAll( chemFileNames ) ;
        fileNames.addAll( mathFileNames ) ;
        
        for( String fileName : fileNames ) {
            log.debug( fileName ) ;
        }
    }
    
    private List<String> generateSuffixes( int startSeq, int numQ, String secId ) {
        
        List<String> suffixes = new ArrayList<>() ;
        
        if( secId.startsWith( "LCT_" ) ) {
            suffixes.add( secId ) ;
        }
        
        for( int i=0; i<numQ; i++ ) {
            suffixes.add( secId + "_" + ( startSeq + i ) ) ;
        }
        
        return suffixes ;
    }

    @Override
    public String getNextSequence() {
        if( nextIndex < fileNames.size() ) {
            String fileName = fileNames.get( nextIndex ) ;
            nextIndex++ ;
            return fileName ;
        }
        return "SEQ_COMPLETED" ;
    }

    @Override
    public void rollbackSequence() {
        if( nextIndex > 0 ) {
            nextIndex-- ;
        }
    }

    @Override
    public boolean isMatchingSequence( String sequence ) {
        return true ;
    }
}
