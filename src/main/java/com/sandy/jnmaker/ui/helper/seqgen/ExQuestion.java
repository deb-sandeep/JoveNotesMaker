package com.sandy.jnmaker.ui.helper.seqgen;

import lombok.Data ;

@Data
public class ExQuestion {
    
    private int     chapterNum     = -1 ;
    private String  exerciseName   = null ;
    private int     questionNum    = 0 ;
    private int     subQuestionNum = -1 ;
    private boolean header         = false ;
    private boolean answer         = false ;
    private int     partNum        = -1 ;
    
    public ExQuestion( String fileName ) {
        parseFileName( fileName ) ;
    }
    
    ExQuestion( ExQuestion original ) {
        this.chapterNum     = original.chapterNum ;
        this.exerciseName   = original.exerciseName ;
        this.questionNum    = original.questionNum ;
        this.subQuestionNum = original.subQuestionNum ;
        this.header         = original.header ;
        this.answer         = original.answer ;
        this.partNum        = original.partNum ;
    }
    
    private void parseFileName( String fileName ) {
        
        String fName = fileName ;
        
        // Strip the file extension
        if( fName.endsWith( ".png" ) ) {
            fName = fileName.substring( 0, fileName.length()-4 ) ;
        }
        
        // If this is a part, 
        if( fName.contains( "(" ) ) {
            int startIndex = fName.indexOf( "(" ) ;
            int endIndex   = fName.indexOf( ")", startIndex ) ;
            
            String partNumStr = fName.substring( startIndex+1, endIndex ) ;
            partNum = Integer.parseInt( partNumStr ) ;
            
            fName = fName.substring( 0, startIndex ) ;
        }
        
        String[] parts = fName.split( "_" ) ;
        
        // Extract the chapter number
        chapterNum = Integer.parseInt( parts[0].substring( 2 ) ) ;
        
        // Extract exercise name
        exerciseName = parts[1].trim() ;
        
        fName = parts[2] ;
        // If this is a header, extract the flag and strip 'Hdr'
        if( fName.endsWith( "Hdr" ) ) {
            header = true ;
            fName = fName.substring( 0, fName.length()-3 ) ;
        }
        else if( fName.endsWith( "Ans" ) ) {
            answer = true ;
            fName = fName.substring( 0, fName.length()-3 ) ;
        }
        
        // Parse question and sub-question number
        if( fName.contains( "." ) ) {
            parts = fName.split( "\\." ) ;
            questionNum = Integer.parseInt( parts[0] ) ;
            subQuestionNum = Integer.parseInt( parts[1] ) ;
        }
        else {
            questionNum = Integer.parseInt( fName ) ;
        }
    }
    
    public String getFileName() {
        
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( "Ch" ).append( chapterNum ).append( "_" ) ;
        buffer.append( exerciseName ).append( "_" ) ;
        buffer.append( questionNum ) ;
        
        if( subQuestionNum != - 1 ) {
            buffer.append( "." ).append( subQuestionNum ) ;
        }
        
        if( header ) {
            buffer.append( "Hdr" ) ;
        }
        
        if( answer ) {
            buffer.append( "Ans" ) ;
        }
        
        if( partNum != -1 ) {
            buffer.append( "(" + partNum + ")" ) ;
        }
        
        buffer.append( ".png" ) ;
        
        return buffer.toString() ;
    }
    
    public String toString() {
        return getFileName() ;
    }
    
    public ExQuestion nextElement() {
        ExQuestion nextQ = new ExQuestion( this ) ;
        
        if( partNum != -1 ) {
            nextQ.partNum++ ;
        }
        else {
            
            if( header ) {
                nextQ.header = false ;
                nextQ.subQuestionNum = 1 ;
            }
            else {
                if( subQuestionNum != -1 ) {
                    nextQ.subQuestionNum++ ;
                }
                else {
                    nextQ.questionNum++ ;
                }
            }
        }
        return nextQ ;
    }
    
    public ExQuestion nextMajorElement() {
        ExQuestion nextQ = new ExQuestion( this ) ;
        nextQ.questionNum++ ;
        nextQ.subQuestionNum = -1 ;
        nextQ.header = false ;
        nextQ.answer = false ;
        return nextQ ;
    }
    
    public ExQuestion nextExercise() {
        ExQuestion nextQ = new ExQuestion( this ) ;
        String name = nextQ.exerciseName ;
        
        if( name.equals( "example" ) ) {
            name = "ExA" ;
        }
        else {
            char lastChar = name.charAt( name.length()-1 ) ;
            lastChar++ ;
            name = name.substring( 0, name.length()-1 ) + lastChar ;
        }
        return nextQ ;
    }
    
    public boolean nextItemNeedsIntervention() {
        if( partNum != -1 ) { return true ; }
        return false ;
    }
    
    public static void main( String[] args ) throws Exception {
        ExQuestion q = new ExQuestion( "Ch1_example_1.png" ) ;
        System.out.println( q ) ;
        
        for( int i=0; i<5; i++ ) {
            q = q.nextExercise() ;
            System.out.println( q ) ;
        }
    }
}


