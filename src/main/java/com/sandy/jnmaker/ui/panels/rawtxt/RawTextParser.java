package com.sandy.jnmaker.ui.panels.rawtxt;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RawTextParser {

    private static final Logger log = Logger.getLogger( RawTextParser.class ) ;

    private final static String PREPROCESS_START_MARKER = "// METANOTE START" ;
    private final static String PREPROCESS_END_MARKER   = "// METANOTE END" ;

    private final String text ;

    public RawTextParser( String text ) {
        this.text = text ;
    }

    public List<String> getParsedMetaNotes( boolean parseWithinMetamarkers ) {

        final List<String> metaNotes = new ArrayList<>() ;
        final String[] lines = text.split( "\\R" ) ;

        boolean preprocStartMarkerEncountered = !parseWithinMetamarkers ;
        boolean inMultiLineContext = false ;
        boolean inRTCContext = false ;

        StringBuilder sb = new StringBuilder() ;

        for( String line : lines ) {
            line = StringUtils.stripEnd( line, null ) ;
            if( !preprocStartMarkerEncountered ) {
                if( line.startsWith( PREPROCESS_START_MARKER ) ) {
                    preprocStartMarkerEncountered = true ;
                }
            }
            else {
                if( line.startsWith( PREPROCESS_END_MARKER ) ) {
                    break ;
                }
                else {
                    if( line.equals( "@rtc" ) ) {
                        inRTCContext = true ;
                        sb = new StringBuilder( "@rtc \n" ) ;
                    }
                    else if( inRTCContext ) {
                        if( line.equals( "@endrtc" ) ) {
                            inRTCContext = false ;
                            metaNotes.add( sb.toString() ) ;
                            sb = null ;
                        }
                        else {
                            sb.append( line ).append( "\n" ) ;
                        }
                    }
                    else if( inMultiLineContext ) {
                        if( line.equals( "--" ) ) {
                            metaNotes.add( sb.toString() ) ;
                            inMultiLineContext = false ;
                            sb = null ;
                        }
                        else {
                            sb.append( line ).append( "\n" ) ;
                        }
                    }
                    else if( line.startsWith( "@qa "           ) ||
                             line.startsWith( "@match"         ) ||
                             line.startsWith( "@choice"        ) ||
                             line.startsWith( "@as-is"         ) ||
                             line.startsWith( "@chem_compound" ) ||
                             line.startsWith( "@false"         ) ||
                             line.startsWith( "@choice_group"  ) ||
                             line.startsWith( "@choice_bulk"   ) ||
                             line.startsWith( "@def"           ) ||
                             line.startsWith( "@context"       ) ) {

                        inMultiLineContext = true ;
                        sb = new StringBuilder() ;
                        sb.append( line ).append( "\n" ) ;
                    }
                    else if( line.startsWith( "@tf false " ) ) {

                        inMultiLineContext = true ;
                        sb = new StringBuilder() ;
                        sb.append( "@tf " )
                          .append( line.substring( "@tf false ".length() ) )
                          .append( "\n" ) ;
                    }
                    else if( line.startsWith( "@fib "          ) ||
                             line.startsWith( "@tf "           ) ||
                             line.startsWith( "@true"          ) ||
                             line.startsWith( "@section "      ) ||
                             line.startsWith( "@chem_equation" ) ) {

                        metaNotes.add( line ) ;
                    }
                }
            }
        }
        return metaNotes ;
    }
}
