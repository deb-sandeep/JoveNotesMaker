package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import com.sandy.common.util.StringUtil;
import com.sandy.jnmaker.ui.panels.rawtxt.RawTextParser;
import com.sandy.jnmaker.util.NoteTextUtil;
import com.sandy.jnmaker.util.NoteType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

import static com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator.*;
import static com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator.autoCreateRTCNode;
import static com.sandy.jnmaker.util.JNSrcTokenizer.FIB;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class RTCAutoCreator {

    private static final Logger log = Logger.getLogger( RTCAutoCreator.class ) ;

    private final String input ;

    private String context ;

    public RTCAutoCreator( String input ) {
        this.input = input ;
    }

    public String createNote() {
        RawTextParser rawTextParser = new RawTextParser( this.input ) ;
        List<String> metaNotes = rawTextParser.getParsedMetaNotes( false ) ;

        StringBuilder sb = new StringBuilder() ;
        sb.append( "\n@rtc {\n" ) ;

        for( String metaNote : metaNotes ) {
            String autoCreatedNote = createRTCFragment( metaNote ) ;
            if( StringUtil.isNotEmptyOrNull( autoCreatedNote ) ) {
                sb.append( autoCreatedNote ) ;
            }
        }

        sb.append( "}\n" ) ;
        return sb.toString() ;
    }

    private String createRTCFragment(String selectedText ) {

        try {
            selectedText = selectedText.trim() ;

            if( selectedText.startsWith( "@context" ) ) {
                return "context\n\"" + NoteTextUtil.formatText( stripTag(selectedText), true ) + "\n\"\n\n" ;
            }
            else if( selectedText.startsWith( "@as-is" ) ) {
                return "\n\n" + stripTag( selectedText ) ;
            }
            else if( selectedText.startsWith( "@fib" ) ) {
                return autoCreateFIBNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@tf" ) ) {
                return autoCreateTrueFalseNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@true" ) ) {
                return autoCreateTrueFalseNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@false" ) ) {
                return autoCreateTrueFalseNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@section" ) ) {
                selectedText = stripTag( selectedText ) ;

                if( selectedText.startsWith( "\"" ) ) {
                    selectedText = selectedText.substring( 1 ) ;
                }
                if( selectedText.endsWith( "\"" ) ) {
                    selectedText = selectedText.substring( 0, selectedText.length()-1 ) ;
                }

                String text = StringUtils.rightPad( "//", 80, '-' ) ;
                text += "\n@section \"" + selectedText + "\"\n\n" ;
                return text ;
            }
            else if( selectedText.startsWith( "@def" ) ) {
                return autoCreateDefinitionNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@qa" ) ) {
                return autoCreateQANote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@match" ) ) {
                return autoCreateMatchingNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@choice_group" ) ) {
                return autoCreateMultiChoiceGroupNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@choice" ) ) {
                return autoCreateMultiChoiceNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@chem_equation" ) ) {
                return autoCreateChemEquationNote( stripTag( selectedText ) ) ;
            }
            else if( selectedText.startsWith( "@chem_compound" ) ) {
                return autoCreateChemCompoundNote( stripTag( selectedText ) ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in deducing question", e ) ;
        }
        return null ;
    }

    private String stripTag( String input ) {

        if( !input.startsWith( "@") ) {
            throw new IllegalArgumentException( "String does not start with @" ) ;
        }
        for( int i=0; i<input.length(); i++ ) {
            char ch = input.charAt( i ) ;
            if( ch == ' ' || ch == '\n' || ch == '\t' ) {
                return input.substring( i + 1 ).trim() ;
            }
        }
        return input ;
    }
}
