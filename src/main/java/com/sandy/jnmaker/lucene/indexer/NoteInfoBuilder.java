package com.sandy.jnmaker.lucene.indexer;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.lucene.ChapterInfo ;
import com.sandy.jnmaker.lucene.NoteInfo ;
import com.sandy.xtext.joveNotes.Character ;
import com.sandy.xtext.joveNotes.ChemCompound ;
import com.sandy.xtext.joveNotes.ChemEquation ;
import com.sandy.xtext.joveNotes.Definition ;
import com.sandy.xtext.joveNotes.Equation ;
import com.sandy.xtext.joveNotes.Event ;
import com.sandy.xtext.joveNotes.Exercise ;
import com.sandy.xtext.joveNotes.ImageLabel ;
import com.sandy.xtext.joveNotes.Matching ;
import com.sandy.xtext.joveNotes.MultiChoice ;
import com.sandy.xtext.joveNotes.NotesElement ;
import com.sandy.xtext.joveNotes.QuestionAnswer ;
import com.sandy.xtext.joveNotes.RefToContext ;
import com.sandy.xtext.joveNotes.Spellbee ;
import com.sandy.xtext.joveNotes.TeacherNote ;
import com.sandy.xtext.joveNotes.TrueFalse ;
import com.sandy.xtext.joveNotes.VoiceToText ;
import com.sandy.xtext.joveNotes.WordMeaning ;

public class NoteInfoBuilder {
    
    static Logger log = Logger.getLogger( NoteInfoBuilder.class ) ;
    
    private static final String JN_MARKER_PATTERN = "\\{\\{@([a-zA-Z0-9]*)\\s+((.(?!\\{\\{))*)\\}\\}" ;

    public static final String QA            = "question_answer" ;
    public static final String WM            = "word_meaning" ;
    public static final String FIB           = "fib" ;
    public static final String DEFINITION    = "definition" ;
    public static final String CHARACTER     = "character" ;
    public static final String TEACHER_NOTE  = "teacher_note" ;
    public static final String MATCHING      = "matching" ;
    public static final String EVENT         = "event" ;
    public static final String TRUE_FALSE    = "true_false" ;
    public static final String CHEM_EQUATION = "chem_equation" ;
    public static final String CHEM_COMPOUND = "chem_compound" ;
    public static final String SPELLBEE      = "spellbee" ;
    public static final String IMAGE_LABEL   = "image_label" ;
    public static final String EQUATION      = "equation" ; 
    public static final String RTC           = "rtc" ;  
    public static final String MULTI_CHOICE  = "multi_choice" ;
    public static final String EXERCISE      = "exercise" ;
    public static final String VOICE2TEXT    = "voice2text" ;
    
    public static NoteInfo build( ChapterInfo ci,  
                                  NotesElement ast,
                                  String srcText,
                                  File srcFile ) 
        throws Exception {
        
        if( ast instanceof TeacherNote ) {
            return null ;
        }
        
        NoteInfo ni = new NoteInfo( ci ) ;
        
        if( ast instanceof QuestionAnswer ){
            ni.setType( QA ) ;
        }
        else if( ast instanceof WordMeaning ){
            ni.setType( WM ) ;
        }
        else if( ast instanceof Definition ){
            ni.setType( DEFINITION ) ;
        }
        else if( ast instanceof Character ){
            ni.setType( CHARACTER ) ;
        }
        else if( ast instanceof Event ){
            ni.setType( EVENT ) ;
        }
        else if( ast instanceof com.sandy.xtext.joveNotes.FIB ){
            ni.setType( FIB ) ;
        }
        else if( ast instanceof Matching ){
            ni.setType( MATCHING ) ;
        }
        else if( ast instanceof TrueFalse ){
            ni.setType( TRUE_FALSE ) ;
        }
        else if( ast instanceof Spellbee ){
            ni.setType( SPELLBEE ) ;
        }
        else if( ast instanceof ImageLabel ){
            ni.setType( IMAGE_LABEL ) ;
        }
        else if( ast instanceof ChemCompound ){
            ni.setType( CHEM_COMPOUND ) ;
        }
        else if( ast instanceof Equation ){
            ni.setType( EQUATION ) ;
        }
        else if( ast instanceof ChemEquation ){
            ni.setType( CHEM_EQUATION ) ;
        }
        else if( ast instanceof RefToContext ){
            ni.setType( RTC ) ;
        }
        else if( ast instanceof MultiChoice ){
            ni.setType( MULTI_CHOICE ) ;
        }
        else if( ast instanceof Exercise ) {
            ni.setType( EXERCISE ) ;
        }
        else if( ast instanceof VoiceToText ) {
            ni.setType( VOICE2TEXT ) ;
        }
        
        ni.setMediaFiles( getMediaFiles( srcFile, srcText ) ) ;
        ni.setContent( srcText ) ;
        return ni ;
    }
    
    public static List<File> getMediaFiles( File file, String noteText )
            throws Exception {

        if( noteText == null ) return null ;

        List<File> mediaFiles = new ArrayList<File>() ;
        Pattern r = Pattern.compile( JN_MARKER_PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( noteText ) ;
        File baseDir = file.getParentFile() ;

        while( m.find() ) {
            String markerType = m.group( 1 ) ;
            String markerData = m.group( 2 ) ;

            processMarker( markerType, markerData, baseDir, mediaFiles ) ;
        }

        return mediaFiles ;
    }

    private static void processMarker( String type, String data, 
                                File baseDir, List<File> mediaFiles ) 
        throws Exception {

        if( type.equals( "img" ) ) {
            processImg( data, baseDir, mediaFiles ) ;
        }
        else if( type.equals( "audio" ) ) {
            processAudio( data, baseDir, mediaFiles ) ;
        }
        else if( type.equals( "doc" ) ) {
            processDoc( data, baseDir, mediaFiles ) ;
        }
    }

    private static void processImg( String imgName, File baseDir, 
                                    List<File> mediaFiles )
            throws Exception {

        // If the image name ends with .cmap.png or .uml.png, we do nothing.
        // This is so because cmap and uml files are generated and stored in
        // the media directory. They are not expected in the source folder.
        if( imgName.endsWith( ".cmap.png" ) || imgName.endsWith( ".uml.png" ) ) { return ; }

        File srcFile = new File( new File( baseDir, "img" ), imgName ) ;
        mediaFiles.add( srcFile ) ;
    }

    private static void processAudio( String audioClipName, File baseDir,
                                      List<File> mediaFiles ) throws Exception {

        File srcFile = new File( new File( baseDir, "audio" ), audioClipName ) ;
        mediaFiles.add( srcFile ) ;
    }

    private static void processDoc( String docName, File baseDir, 
                                    List<File> mediaFiles )
            throws Exception {

        if( docName.indexOf( '|' ) != -1 ) {
            docName = docName.substring( 0, docName.indexOf( '|' ) ).trim() ;
        }

        File srcFile = new File( new File( baseDir, "doc" ), docName ) ;
        mediaFiles.add( srcFile ) ;
    }
}