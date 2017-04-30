package com.sandy.jnmaker.tools.mapping;

import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.tools.AbstractTool ;
import com.sandy.jnmaker.tools.AbstractToolConfigPanel ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.panels.jn.JoveNotesPanel ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class MatrixMappingTool extends AbstractTool {
    
    public static Logger logger = Logger.getLogger( MatrixMappingTool.class ) ;
    
    private MappingToolConfigPanel configPanel = null ;
    
    public MatrixMappingTool() {
        super( "Matrix Mapping Tool" ) ;
    }
    
    @Override
    protected AbstractToolConfigPanel getConfigPanel() {
        if( configPanel == null ) {
            configPanel = new MappingToolConfigPanel() ;
        }
        return configPanel ;
    }

    @Override
    protected void executeTool() throws Exception {
        
        MainFrame      mainFrame = ObjectRepository.getMainFrame() ;
        JoveNotesPanel jnPanel   = mainFrame.getJNPanel() ;
        
        jnPanel.addNote( collateNotes( configPanel.getParser() ) ) ;
    }

    private String collateNotes( MappingFileParser parser ) {
        
        StringBuilder buffer = new StringBuilder() ;
        
        if( configPanel.isR2CMappingEnabled() ) {
            if( configPanel.isQAForR2CMappingEnabled() ) {
                collateQAForR2CMappings(  parser, buffer ) ;
            }
            
            if( configPanel.isMLCForR2CMappingEnabled() ) {
                collateMLCForR2CMappings( parser, buffer ) ;
            }
            
            if( configPanel.isFIBForR2CMappingEnabled() ) {
                collateFIBForR2CMappings( parser, buffer ) ;
            }
        }
        
        if( configPanel.isC2RMappingEnabled() ) {
            if( configPanel.isQAForR2CMappingEnabled() ) {
                collateQAForC2RMappings( parser, buffer ) ;
            }
            
            if( configPanel.isMLCForC2RMappingEnabled() ) {
                collateMLCForC2RMappings( parser, buffer ) ;
            }
            
            if( configPanel.isFIBForC2RMappingEnabled() ) {
                collateFIBForC2RMappings( parser, buffer ) ;
            }
        }
        return buffer.toString() ;
    }

    private void collateQAForR2CMappings( MappingFileParser parser,
            StringBuilder buffer ) {
        
        String qaTemplate = configPanel.getQATemplateForR2CMapping() ;
        
        for( String row : parser.getRowNames() ) {
            
            String question = qaTemplate.replace( "<row>", row ) ;
            
            List<String> colsForRow = parser.getColumnsForRow( row ) ;
            if( colsForRow.isEmpty() ) {
                continue ;
            }
            
            StringBuilder ansBuffer = new StringBuilder( "\n" ) ;
            for( int i=0; i<colsForRow.size(); i++ ) {
                ansBuffer.append( i+1 )
                .append( ". " )
                .append( colsForRow.get( i ) )
                .append( "\n" ) ;
            }
            
            buffer.append( "@qa \"" )
            .append( question )
            .append( "\"\n" )
            .append( "\"" )
            .append( ansBuffer )
            .append( "\"\n\n" ) ;
        }
    }
    
    private void collateQAForC2RMappings( MappingFileParser parser,
                                          StringBuilder buffer ) {
        
        String qaTemplate = configPanel.getQATemplateForC2RMapping() ;
        
        for( String col : parser.getColNames() ) {
            
            String question = qaTemplate.replace( "<col>", col ) ;
            
            List<String> rowsForCol = parser.getRowsForColumn( col ) ;
            if( rowsForCol.isEmpty() ) {
                continue ;
            }
            
            StringBuilder ansBuffer = new StringBuilder( "\n" ) ;
            for( int i=0; i<rowsForCol.size(); i++ ) {
                ansBuffer.append( i+1 )
                         .append( ". " )
                         .append( rowsForCol.get( i ) )
                         .append( "\n" ) ;
            }
            
            buffer.append( "@qa \"" )
                  .append( question )
                  .append( "\"\n" )
                  .append( "\"" )
                  .append( ansBuffer )
                  .append( "\"\n\n" ) ;
        }
    }

    private void collateMLCForR2CMappings( MappingFileParser parser,
                                           StringBuilder buffer ) {
        
        String mcCaptionTemplate = configPanel.getQATemplateForR2CMapping() ;
        
        for( String row : parser.getRowNames() ) {
            
            String caption = mcCaptionTemplate.replace( "<row>", row ) ;
            
            List<String> colNames   = parser.getColNames() ;
            List<String> colsForRow = parser.getColumnsForRow( row ) ;
            
            if( colsForRow.isEmpty() ) {
                continue ;
            }
            
            buffer.append( "@multi_choice \"" )
                  .append( caption )
                  .append( "\" {\n" )
                  .append( "    @options {\n" ) ;
            
            for( int i=0; i<colNames.size(); i++ ) {
                buffer.append( "        \"" )
                      .append( colNames.get( i ) )
                      .append( "\"" ) ;
                if( colsForRow.contains( colNames.get( i ) ) ) {
                    buffer.append( " correct" ) ;
                }
                if( i < colNames.size()-1 ) {
                    buffer.append( "," ) ;
                }
                buffer.append( "\n" ) ;
            }
            
            int numOptionsToShow = getNormalizedNumOptionsToShow( 
                                    configPanel.getMLCNumOptionsToShowForR2C(), 
                                    colsForRow, colNames ) ;
            
            buffer.append( "    }\n" )
                  .append( "    @numOptionsToShow " )
                  .append( numOptionsToShow )
                  .append( "\n" )
                  .append( "    @numOptionsPerRow " )
                  .append( configPanel.getMLCNumOptionsPerRowForR2C() )
                  .append( "\n" )
                  .append( "}\n\n" ) ;
        }
    }
    
    private void collateMLCForC2RMappings( MappingFileParser parser,
                                           StringBuilder buffer ) {

        String mcCaptionTemplate = configPanel.getQATemplateForC2RMapping();

        for( String col : parser.getColNames() ) {

            String caption = mcCaptionTemplate.replace( "<col>", col ) ;

            List<String> rowNames   = parser.getRowNames();
            List<String> rowsForCol = parser.getRowsForColumn( col ) ;
            
            if( rowsForCol.isEmpty() ) {
                continue ;
            }

            buffer.append( "@multi_choice \"" )
                  .append( caption )
                  .append( "\" {\n" )
                  .append( "    @options {\n" ) ;

            for( int i = 0 ; i < rowNames.size() ; i++ ) {
                buffer.append( "        \"" )
                      .append( rowNames.get( i ) )
                      .append( "\"" ) ;
                
                if( rowsForCol.contains( rowNames.get( i ) ) ) {
                    buffer.append( " correct" ) ;
                }
                
                if( i < rowNames.size() - 1 ) {
                    buffer.append( "," ) ;
                }
                
                buffer.append( "\n" ) ;
            }

            int numOptionsToShow = getNormalizedNumOptionsToShow(
                                    configPanel.getMLCNumOptionsToShowForR2C(), 
                                    rowsForCol, rowNames );

            buffer.append( "    }\n" )
                  .append( "    @numOptionsToShow " )
                  .append( numOptionsToShow )
                  .append( "\n" )
                  .append( "    @numOptionsPerRow " )
                  .append( configPanel.getMLCNumOptionsPerRowForR2C() )
                  .append( "\n" )
                  .append( "}\n\n" );
        }
    }

    private int getNormalizedNumOptionsToShow( int configValue, 
                                               List<String> correctAnswers,
                                               List<String> allAnswers ) {
        int normValue = configValue ;
        
        if( normValue <= correctAnswers.size() ) {
            normValue = correctAnswers.size() + 2 ;
        }
        
        if( normValue > allAnswers.size() ) {
            normValue = allAnswers.size() ;
        }
        
        return normValue ;
    }
    
    private void collateFIBForR2CMappings( MappingFileParser parser,
                                           StringBuilder buffer ) {
        
        String fibTemplate = configPanel.getFIBTemplateForR2CMapping() ;
        
        for( String row : parser.getRowNames() ) {
            
            List<String> colsForRow = parser.getColumnsForRow( row ) ;
            if( colsForRow.isEmpty() ) {
                continue ;
            }
            
            StringBuilder blanksBuffer = new StringBuilder() ;
            for( int i=0; i<colsForRow.size()-1; i++ ) {
                blanksBuffer.append( "{" + i + "}" ) ;
                if( i < colsForRow.size()-2 ) {
                    blanksBuffer.append( ", " ) ;
                }
            }
            if( colsForRow.size() > 1 ) {
                blanksBuffer.append( " and " ) ;
            }
            blanksBuffer.append( "{" + (colsForRow.size()-1) + "}" ) ;
            
            String caption = fibTemplate.replace( "<row>", row ) ;
            caption = caption.replace( "<blanks>", blanksBuffer ) ;
            
            buffer.append( "@fib \"" )
                  .append( caption )
                  .append( "\"\n" ) ;
            
            for( int i=0; i<colsForRow.size(); i++ ) {
                buffer.append( "\"" )
                      .append( colsForRow.get( i ) )
                      .append( "\"\n" ) ;
            }
            buffer.append( "\n" ) ;
        }
    }

    private void collateFIBForC2RMappings( MappingFileParser parser,
                                           StringBuilder buffer ) {

        String fibTemplate = configPanel.getFIBTemplateForC2RMapping() ;

        for( String col : parser.getColNames() ) {

            List<String> rowsForCol = parser.getRowsForColumn( col ) ;
            if( rowsForCol.isEmpty() ) {
                continue ;
            }

            StringBuilder blanksBuffer = new StringBuilder() ;
            for( int i = 0 ; i < rowsForCol.size() - 1 ; i++ ) {
                blanksBuffer.append( "{" + i + "}" ) ;
                if( i < rowsForCol.size() - 2 ) {
                    blanksBuffer.append( ", " ) ;
                }
            }
            if( rowsForCol.size() > 1 ) {
                blanksBuffer.append( " and " ) ;
            }
            
            blanksBuffer.append( "{" + ( rowsForCol.size() - 1 ) + "}" ) ;

            String caption = fibTemplate.replace( "<col>", col ) ;
            caption = caption.replace( "<blanks>", blanksBuffer ) ;

            buffer.append( "@fib \"" )
                  .append( caption )
                  .append( "\"\n" ) ;

            for( int i = 0; i < rowsForCol.size(); i++ ) {
                buffer.append( "\"" )
                      .append( rowsForCol.get( i ) )
                      .append( "\"\n" ) ;
            }
            buffer.append( "\n" ) ;
        }
    }
}
