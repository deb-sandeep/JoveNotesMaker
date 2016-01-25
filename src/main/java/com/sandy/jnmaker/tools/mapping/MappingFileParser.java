package com.sandy.jnmaker.tools.mapping;

import java.io.File ;
import java.io.IOException ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.commons.io.FileUtils ;

import com.sandy.common.util.StringUtil ;

public class MappingFileParser {
    
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

    private File         inputFile = null ;
    private List<String> colNames  = new ArrayList<>() ;
    private List<String> rowNames  = new ArrayList<>() ;
    private boolean[][]  mappings  = null ;
    private int          curMapRow = 0 ;
    
    private enum ParseState { OPEN, ROWS, COLS, MAPPING } ;
    private ParseState parseState = ParseState.OPEN ;

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
            switch( parseState ) {
                case COLS:
                    colNames.add( line.trim() ) ;
                    break ;
                case ROWS:
                    rowNames.add( line.trim() ) ;
                    break ;
                case MAPPING:
                    parseMappingLine( line, msgs ) ;
                    break ;
                case OPEN:
                    msgs.add( "Don't know what to do with line - " + line ) ;
                    break ;
            }
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
                
            case "colNames" :
                this.parseState = ParseState.COLS ;
                break ;
                
            case "rowNames" :
                this.parseState = ParseState.ROWS ;
                break ;
                
            case "mappings" :
                this.parseState = ParseState.MAPPING ;
                mappings = new boolean[getNumRowsInFile()][getNumColsInFile()] ;
                break ;
        }
    }
    
    private void parseMappingLine( String line, List<String> msgs ) {
        String[] cols = line.split( "\\s+" ) ;
        if( cols.length < getNumColsInFile() ) {
            msgs.add( "Mapping line " + curMapRow + " has less number of columns." ) ;
        }
        else {
            for( int i=0; i<cols.length; i++ ) {
                mappings[curMapRow][i] = false ;
                if( cols[i].equals( "1" ) ) {
                    mappings[curMapRow][i] = true ;
                }
            }
        }
        curMapRow++ ;
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
    
    public File getInputFile() {
        return this.inputFile ;
    }
    
    public int getNumColsInFile() {
        return this.colNames.size() ;
    }
    
    public int getNumRowsInFile() {
        return this.rowNames.size() ;
    }
    
    public List<String> getColNames() {
        return this.colNames ;
    }
    
    public List<String> getRowNames() {
        return this.rowNames ;
    }
    
    public List<String> getColumnsForRow( String row ) {
        List<String> cols = new ArrayList<>() ;
        int rowIndex = rowNames.indexOf( row ) ;
        if( rowIndex != -1 ) {
            boolean[] mappingRow = mappings[rowIndex] ;
            for( int i=0; i<colNames.size(); i++ ) {
                if( mappingRow[i] == true ) {
                    cols.add( colNames.get( i ) ) ;
                }
            }
        }
        return cols ;
    }
    
    public List<String> getRowsForColumn( String col ) {
        List<String> rows = new ArrayList<>() ;
        int colIndex = colNames.indexOf( col ) ;
        if( colIndex != -1 ) {
            for( int i=0; i<rowNames.size(); i++ ) {
                if( mappings[i][colIndex] == true ) {
                    rows.add( rowNames.get( i ) ) ;
                }
            }
        }
        return rows ;
    }
}
