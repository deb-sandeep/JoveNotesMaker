package com.sandy.jnmaker.ui.panels.image.k12;

import com.sandy.jeecoach.util.AbstractQuestion ;

import lombok.Data ;
import lombok.EqualsAndHashCode ;

@Data
@EqualsAndHashCode( callSuper = false )
public class K12ExerciseQuestion extends AbstractQuestion {
    
    private int     chapterNum     = -1 ;
    private String  exerciseName   = null ;
    private int     questionNum    = 0 ;
    private int     subQuestionNum = -1 ;
    private boolean header         = false ;
    private boolean answer         = false ;
    private int     partNum        = -1 ;
    
    public K12ExerciseQuestion( String fileName ) {
        parseFileName( fileName ) ;
    }
    
    K12ExerciseQuestion( K12ExerciseQuestion original ) {
        this.chapterNum     = original.chapterNum ;
        this.exerciseName   = original.exerciseName ;
        this.questionNum    = original.questionNum ;
        this.subQuestionNum = original.subQuestionNum ;
        this.header         = original.header ;
        this.answer         = original.answer ;
        this.partNum        = original.partNum ;
    }
    
    // File format expected as follows
    // Ch<chapterNum>_<exerciseName>_[questionNum.subQuestionNum][Hdr|Ans](partNum).png
    //
    // * chapterNum should be convertible to integer
    // * exerciseName is free format but the convention is Ex[A|B|C|D|..]
    // * questionNum is integer and is mandatory
    // * .subQuestionNum is the decimal part and is optional
    // * If Hdr is present as suffix - this is considered a header part
    // * If Ans is present as suffix - this is considered as answer
    // * (partNum) is optional and if present represents a part fragment of 
    //   the given sequence number
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
    
    public AbstractQuestion nextQuestion() {
        K12ExerciseQuestion nextQ = new K12ExerciseQuestion( this ) ;
        
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
    
    public K12ExerciseQuestion nextMajorElement() {
        K12ExerciseQuestion nextQ = new K12ExerciseQuestion( this ) ;
        nextQ.questionNum++ ;
        nextQ.subQuestionNum = -1 ;
        nextQ.header = false ;
        nextQ.answer = false ;
        return nextQ ;
    }
    
    public K12ExerciseQuestion nextExercise() {
        K12ExerciseQuestion nextQ = new K12ExerciseQuestion( this ) ;
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
    
    public static void main( String[] args ) throws Exception {
        K12ExerciseQuestion q = new K12ExerciseQuestion( "Ch1_example_1.png" ) ;
        System.out.println( q ) ;
        
        for( int i=0; i<5; i++ ) {
            q = q.nextExercise() ;
            System.out.println( q ) ;
        }
    }
}


