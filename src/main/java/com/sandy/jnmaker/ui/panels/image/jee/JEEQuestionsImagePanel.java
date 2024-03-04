package com.sandy.jnmaker.ui.panels.image.jee;

import static java.awt.event.KeyEvent.VK_1 ;
import static java.awt.event.KeyEvent.VK_2 ;
import static java.awt.event.KeyEvent.VK_3 ;
import static java.awt.event.KeyEvent.VK_4 ;

import java.awt.event.ActionListener ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;

import javax.swing.JComponent ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jeecoach.util.JEEQuestionImage ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.SaveFileNameHelperAccessory ;
import com.sandy.jnmaker.ui.panels.image.SaveFnKeyHandler ;
import com.sandy.jnmaker.util.AppConfig ;
import com.sandy.jnmaker.util.ObjectRepository ;

@SuppressWarnings( "serial" )
public class JEEQuestionsImagePanel extends AbstractImagePanel<JEEQuestionImage> 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( JEEQuestionsImagePanel.class ) ;
    
    public static final String ID = "JEE" ;
    
    private final SaveFnKeyHandler cycleQuestionType =
        new SaveFnKeyHandler( "cycleQuestionType" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    JEEQuestionImage nextQ = new JEEQuestionImage( selFile ) ;
                    nextQ.setQuestionType( nextQ.getNextQuestionType() ) ;
                    if( nextQ.isLCT() ) {
                        nextQ.setLctSequence( lastLCTSequenceNumber+1 ) ;
                    }
                    else {
                        nextQ.setLctSequence( -1 ) ;
                    }
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                }
            }
        } ;
        
    private final SaveFnKeyHandler cycleSection =
        new SaveFnKeyHandler( "cycleSection" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    JEEQuestionImage nextQ = new JEEQuestionImage( selFile ) ;
                    nextQ.getQId().setSectionId( nextQ.getNextSectionName() ) ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                }
            }
        } ; 
                
    private final SaveFnKeyHandler incrementQuestionNumber =
        new SaveFnKeyHandler( "incrementQuestionNumber" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    JEEQuestionImage nextQ = new JEEQuestionImage( selFile ) ;
                    nextQ = nextQ.nextQuestion() ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                }
            }
        } ; 
        
    private final SaveFnKeyHandler setPart =
        new SaveFnKeyHandler( "setPart" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    JEEQuestionImage nextQ = new JEEQuestionImage( selFile ) ;
                    nextQ.setPartNumber( 1 ) ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                }
            }
        } ; 
                
    private int lastLCTSequenceNumber = -1 ;
        
    public JEEQuestionsImagePanel() {
        super() ;
        bindKeyStrokesForSaveDialog() ;
    }
    
    private void bindKeyStrokesForSaveDialog() {
        
        super.registerSaveFnHandler( VK_1, cycleQuestionType ) ;
        super.registerSaveFnHandler( VK_2, cycleSection ) ;
        super.registerSaveFnHandler( VK_3, incrementQuestionNumber ) ;
        super.registerSaveFnHandler( VK_4, setPart ) ;
    }
    
    @Override
    protected JEEQuestionImage constructQuestion( File file ) {
        return new JEEQuestionImage( file ) ;
    }

    @Override
    protected JComponent getSaveFileChooserAccessory() {
        String[] help = {
            "File name format :",
            "----------------------------",
            "1. [P|M|C]  - Subject code",
            "2. <int>    - Standard",
            "3. <String> - Book code",
            "   - PF > Pearson",
            "   - MR > MTG Reasoning",
            "4. <int>    - Chapter number",
            "5. <String> - Question type",
            "   - SCA, MCA, NT, LCT, ..",
            "6. [LCT#]\n",
            
            "----------- PR ------------",
            "7. Section",
            "   - VSAT > Very short answer",
            "   - SAT > Short answer",
            "   - ETQ > Essay type question",
            "   - CA_n > Concept application",
            "   - AT_n > Assessment type",
            "9. CA|AT section number",
            "9/10. Question number\n",
            
            "----------- MR ------------",
            "6. Question number\n\n",
            
            "----------- Save shortcuts --",
            "Ctrl+1 - Increment QType",
            "Ctrl+2 - Increment Section",
            "Ctrl+3 - Increment QNo",
            "Ctrl+4 - Set part"
        } ;
        return new SaveFileNameHelperAccessory( help ) ;
    }
    
    public File getRecommendedSaveDir( File imgFile ) {
        return new File( imgFile.getParentFile(), "scrapes" ) ;
    }
    
    protected void handleLastQuestionSave( JEEQuestionImage lastQuestion ) {
        if( lastQuestion.isLCT() ) {
            lastLCTSequenceNumber = lastQuestion.getLctSequence() ;
        }
    }
    
    protected void writeSelectedImageToFile( BufferedImage image,
                                             File outputFile ) {
        
        super.writeSelectedImageToFile( image, outputFile ) ;
        
        try {
            AppConfig cfg = ObjectRepository.getAppConfig() ;
            File jeeImageLogFile = cfg.getJeeImageNameSaveFile() ;
            
            String outTxt = outputFile.getName() ;
            outTxt = outTxt.substring( 0, outTxt.length()-4 ) ;
            outTxt = outTxt + "\n" ;
            FileUtils.write( jeeImageLogFile, outTxt, true ) ;
        }
        catch( IOException e ) {
            log.error( "Img log could not be written.", e ) ;
        }
    }
}
