package com.sandy.jnmaker.tools.mapping;

import java.io.File ;
import java.io.IOException ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

public class MappingFileParser {
    
    private static final Logger logger = Logger.getLogger( MappingFileParser.class ) ;
    private static final Pattern CONFIG_PARAM_PATTERN = 
                         Pattern.compile( "^@([a-zA-Z0-9_]+)\\s*=?\\s*(.*)$" ) ;
    
    private String  colName                 = "Column" ;
    private String  rowName                 = "Row" ;

    private boolean enableC2RMappings       = true ;
    private boolean enable_MC_R2C           = true ;
    private int     numOptionsPerRow_MC_R2C = 4 ;
    private int     numOptionsToShow_MC_R2C = 12 ;
    private String  caption_MC_R2C          = "Match the $colName$s for <row>.";

    private boolean enable_FIB_R2C          = true ;
    private String  template_FIB_R2C        = "The $colName$s for <row> are $blanks$" ;
    
    private boolean enable_QA_R2C           = true ;
    private String  template_QA_R2C         = "What are the $colName$s for <row>?" ;

    private boolean enableR2CMappings       = true ;
    private boolean enable_MC_C2R           = true ;
    private int     numOptionsPerRow_MC_C2R = 4 ;
    private int     numOptionsToShow_MC_C2R = 12 ;
    private String  caption_MC_C2R          = "Match the $rowName$s for <col>.";
    
    private boolean enable_FIB_C2R          = true ;
    private String  template_FIB_C2R        = "The $rowName$s for <col> are $blanks$" ;

    private boolean enable_QA_C2R           = true ;
    private String  template_QA_C2R         = "What are the $colName$s for <row>?" ;

    private File inputFile = null ;

    public MappingFileParser( File inputFile ) {
        this.inputFile = inputFile ;
    }

    public List<String> parse() {
        List<String> msgs = new ArrayList<>() ; 
        
        if( this.inputFile == null || !this.inputFile.exists() ) {
            msgs.add( "Input file " + this.inputFile.getAbsolutePath() + " not found." ) ;
        }
        else {
            try {
                List<String> lines = FileUtils.readLines( this.inputFile ) ;
                for( String line : lines ) {
                    if( StringUtil.isEmptyOrNull( line ) ) {
                        continue ;
                    }
                    else if( line.trim().startsWith( "//" ) ) {
                        continue ;
                    }
                    else {
                        parseLine( line, msgs ) ;
                    }
                }
            }
            catch( IOException e ) {
                msgs.add( "Error reading file.\nMsg = " + e.getMessage() ) ;
            }
        }
        
        return msgs ;
    }

    private void parseLine( String line, List<String> msgs ) {
        
        Matcher matcher = CONFIG_PARAM_PATTERN.matcher( line ) ;
        if( matcher.matches() ) {
            String paramName = matcher.group( 1 ) ;
            String paramVal  = matcher.group( 2 ) ;
            parseConfigParameter( paramName, paramVal, msgs ) ;
        }
        else {
            logger.debug( "Non config line = " + line ) ;
        }
    }
    
    private void parseConfigParameter( String paramName, String paramVal, 
                                       List<String> msgs ) {
        switch( paramName ) {
            case "colName" :
                this.colName = paramVal ; 
                break ;
            
            case "rowName" :
                this.rowName = paramVal ; 
                break ;
            
            case "enableC2RMappings" :
                this.enableC2RMappings = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "enable_MC_R2C" :
                this.enable_MC_R2C = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "numOptionsPerRow_MC_R2C" :
                this.numOptionsPerRow_MC_R2C = 
                           getInt( "numOptionsPerRow_MC_R2C", paramVal, msgs ) ;
                break ;
            
            case "numOptionsToShow_MC_R2C" :
                this.numOptionsToShow_MC_R2C = 
                           getInt( "numOptionsToShow_MC_R2C", paramVal, msgs ) ;
                break ;
            
            case "caption_MC_R2C" :
                this.caption_MC_R2C = paramVal ; 
                break ;
            
            case "enable_FIB_R2C" :
                this.enable_FIB_R2C = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "template_FIB_R2C" :
                this.template_FIB_R2C = paramVal ; 
                break ;
            
            case "enable_QA_R2C" :
                this.enable_QA_R2C = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "template_QA_R2C" :
                this.template_QA_R2C = paramVal ; 
                break ;
            
            case "enableR2CMappings" :
                this.enableR2CMappings = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "enable_MC_C2R" :
                this.enable_MC_C2R = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "numOptionsPerRow_MC_C2R" :
                this.numOptionsPerRow_MC_C2R = 
                           getInt( "numOptionsPerRow_MC_C2R", paramVal, msgs ) ; 
                break ;
            
            case "numOptionsToShow_MC_C2R" :
                this.numOptionsToShow_MC_C2R = 
                           getInt( "numOptionsToShow_MC_C2R", paramVal, msgs ) ;
                break ;
            
            case "caption_MC_C2R" :
                this.caption_MC_C2R = paramVal ; 
                break ;
            
            case "enable_FIB_C2R" :
                this.enable_FIB_C2R = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "template_FIB_C2R" :
                this.template_FIB_C2R = paramVal ; 
                break ;
            
            case "enable_QA_C2R" :
                this.enable_QA_C2R = Boolean.parseBoolean( paramVal ) ; 
                break ;
            
            case "template_QA_C2R" :
                this.template_QA_C2R = paramVal ; 
                break ;
        }
    }
    
    private int getInt( String config, String val, List<String> msgs ) {
        int retVal = -1 ;
        if( StringUtil.isNotEmptyOrNull( val ) ) {
            try {
                retVal = Integer.parseInt( val.trim() ) ;
            }
            catch( Exception e ) {
                msgs.add( "Invalid int value for config " + config ) ;
            }
        }
        return retVal ;
    }
    
    public String getColName() {
        return colName;
    }

    public String getRowName() {
        return rowName;
    }

    public boolean isEnableC2RMappings() {
        return enableC2RMappings;
    }

    public boolean isEnable_MC_R2C() {
        return enable_MC_R2C;
    }

    public int getNumOptionsPerRow_MC_R2C() {
        return numOptionsPerRow_MC_R2C;
    }

    public int getNumOptionsToShow_MC_R2C() {
        return numOptionsToShow_MC_R2C;
    }

    public String getCaption_MC_R2C() {
        return caption_MC_R2C;
    }

    public boolean isEnable_FIB_R2C() {
        return enable_FIB_R2C;
    }

    public String getTemplate_FIB_R2C() {
        return template_FIB_R2C;
    }

    public boolean isEnable_QA_R2C() {
        return enable_QA_R2C;
    }

    public String getTemplate_QA_R2C() {
        return template_QA_R2C;
    }

    public boolean isEnableR2CMappings() {
        return enableR2CMappings;
    }

    public boolean isEnable_MC_C2R() {
        return enable_MC_C2R;
    }

    public int getNumOptionsPerRow_MC_C2R() {
        return numOptionsPerRow_MC_C2R;
    }

    public int getNumOptionsToShow_MC_C2R() {
        return numOptionsToShow_MC_C2R;
    }

    public String getCaption_MC_C2R() {
        return caption_MC_C2R;
    }

    public boolean isEnable_FIB_C2R() {
        return enable_FIB_C2R;
    }

    public String getTemplate_FIB_C2R() {
        return template_FIB_C2R;
    }

    public boolean isEnable_QA_C2R() {
        return enable_QA_C2R;
    }

    public String getTemplate_QA_C2R() {
        return template_QA_C2R;
    }
}
