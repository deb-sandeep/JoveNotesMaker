package com.sandy.jnmaker.tools.mapping;

import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.JFileChooser ;
import javax.swing.JTextField ;
import javax.swing.filechooser.FileFilter ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

public class MappingToolConfigPanel extends MappingToolConfigPanelUI {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger( MappingToolConfigPanel.class ) ;
    
    private JFileChooser fileChooser = new JFileChooser() ;
    private File         currentDir  = new File( System.getProperty( "user.home" ) ) ;
    private MappingFileParser parser = null ;
    
    public MappingToolConfigPanel() {
        super() ;
        setUpFileChooser() ;
        setUpListeners() ;
    }

    protected Dimension getPreferredDialogSize() {
        return new Dimension( 600, 525 ) ;
    }
    
    public File getInputFile() {
        return null ;
    }
    
    // ----------- Mapping input details ---------------------------------------
    public int getNumColumns() {
        return getInt( super.numColsInFileTF ) ;
    }
    
    public void setNumColumns( int numCol ) {
        super.numColsInFileTF.setText( Integer.toString( numCol ) ) ;
    }
    
    public int getNumRows() {
        return getInt( super.numRowsInFileTF ) ;
    }
    
    public void setNumRows( int numRows ) {
        super.numRowsInFileTF.setText( Integer.toString( numRows ) ) ;
    }
    
    public String getColumnName() {
        return super.colNameTF.getText().trim() ;
    }
    
    public void setColumnName( String text ) {
        super.colNameTF.setText( text ) ;
    }
    
    public String getRowName() {
        return super.rowNameTF.getText().trim();
    }
    
    public void setRowName( String text ) {
        super.rowNameTF.setText( text ) ;
    }
    
    // =========================================================================
    // ----------- Row to column mapping ---------------------------------------
    public boolean isR2CMappingEnabled() {
        return super.enableR2CMappings.isSelected() ;
    }
    
    // ----------- Row to column mapping ( Multi Choice ) ----------------------
    public boolean isMLCForR2CMappingEnabled() {
        return isR2CMappingEnabled() && super.enable_MC_R2C.isEnabled() ;
    }
    
    public String getMLCCaptionForR2C() {
        return super.caption_MC_R2C.getText() ;
    }
    
    public int getMLCNumOptionsToShowForR2C() {
        int numOptionsToShow = getInt( super.numOptionsToShow_MC_R2C ) ;
        if( numOptionsToShow == -1 ) {
            numOptionsToShow = getNumColumns() ;
        }
        return numOptionsToShow ;
    }
    
    public int getMLCNumOptionsPerRowForR2C() {
        int numOptionsPerRow = getInt( super.numOptionsPerRow_MC_R2C ) ;
        if( numOptionsPerRow == -1 ) {
            numOptionsPerRow = 4 ;
        }
        return numOptionsPerRow ;
    }
    
    // ----------- Row to column mapping ( Fill in the blanks ) ----------------
    public boolean isFIBForR2CMappingEnabled() {
        return isR2CMappingEnabled() && super.enable_FIB_R2C.isEnabled() ;
    }
    
    public String getFIBTemplateForR2CMapping() {
        return super.template_FIB_R2C.getText() ;
    }
    
    // ----------- Row to column mapping ( Question Answers ) ------------------
    public boolean isQAForR2CMappingEnabled() {
        return isR2CMappingEnabled() && super.enable_QA_R2C.isEnabled() ;
    }
    
    public String getQATemplateForR2CMapping() {
        return super.template_QA_R2C.getText() ;
    }
    
    // =========================================================================
    // ----------- Column to Row mapping ---------------------------------------
    public boolean isC2RMappingEnabled() {
        return super.enableC2RMappings.isSelected() ;
    }
    
    // ----------- Column to Row mapping ( Multi Choice ) ----------------------
    public boolean isMLCForC2RMappingEnabled() {
        return isC2RMappingEnabled() && super.enable_MC_C2R.isEnabled() ;
    }
    
    public String getMLCCaptionForC2R() {
        return super.caption_MC_C2R.getText() ;
    }
    
    public int getMLCNumOptionsToShowForC2R() {
        int numOptionsToShow = getInt( super.numOptionsToShow_MC_C2R ) ;
        if( numOptionsToShow == -1 ) {
            numOptionsToShow = getNumColumns() ;
        }
        return numOptionsToShow ;
    }
    
    public int getMLCNumOptionsPerRowForC2R() {
        int numOptionsPerRow = getInt( super.numOptionsPerRow_MC_C2R ) ;
        if( numOptionsPerRow == -1 ) {
            numOptionsPerRow = 4 ;
        }
        return numOptionsPerRow ;
    }
    
    // ----------- Column to Row mapping ( Fill in the blanks ) ----------------
    public boolean isFIBForC2RMappingEnabled() {
        return isC2RMappingEnabled() && super.enable_FIB_C2R.isEnabled() ;
    }
    
    public String getFIBTemplateForC2RMapping() {
        return super.template_FIB_C2R.getText() ;
    }
    
    // ----------- Column to Row mapping ( Question Answers ) ------------------
    public boolean isQAForC2RMappingEnabled() {
        return isC2RMappingEnabled() && super.enable_QA_C2R.isEnabled() ;
    }
    
    public String getQATemplateForC2RMapping() {
        return super.template_QA_C2R.getText() ;
    }
    
    public List<String> validateUserInput() {
        List<String> msgs = new ArrayList<>() ;
        return msgs ;
    }
    
    private void setUpListeners() {
        super.loadFileBtn.addActionListener( new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                loadFile() ;
            }
        } ) ;
    }
    
    private int getInt( JTextField tf ) {
        int retVal = -1 ;
        String txt = tf.getText() ;
        if( StringUtil.isNotEmptyOrNull( txt ) ) {
            try {
                retVal = Integer.parseInt( txt.trim() ) ;
            }
            catch( Exception e ) {
                logger.error( "Invalid integer value specified.", e ) ;
            }
        }
        return retVal ;
    }
    
    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "Mapping file (.txt only)." ;
            }
            
            @Override
            public boolean accept( File file ) {
                if( file.isDirectory() || file.getName().endsWith( ".txt" ) ) {
                    return true ;
                }
                return false ;
            }
        } ) ;
    }
    
    private File getSelectedFile() {
        
        File selectedFile = null ;
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        int userChoice = fileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            selectedFile = fileChooser.getSelectedFile() ;
        }
        return selectedFile ;
    }
    
    private void loadFile() {
        
        File selectedFile = getSelectedFile() ;
        if( selectedFile != null && selectedFile.exists() ) {
            parser = new MappingFileParser( selectedFile ) ;
            List<String> errMsgs = parser.parse() ;
            if( errMsgs != null && !errMsgs.isEmpty() ) {
                getParentDialog().showErrorMessages( "Invalid file", errMsgs ) ;
            }
            else {
                populateUIFromParsedValues() ;
            }
        }
    }

    private void populateUIFromParsedValues() {
        // TODO Auto-generated method stub
    }
}
