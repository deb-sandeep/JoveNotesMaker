package com.sandy.jnmaker.ui.panels.image.jee;

import java.awt.event.ActionListener ;

import javax.swing.JComponent ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jeecoach.util.JEEQuestion ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.SaveFileNameHelperAccessory ;

@SuppressWarnings( "serial" )
public class JEEQuestionsImagePanel extends AbstractImagePanel<JEEQuestion> 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( JEEQuestionsImagePanel.class ) ;
    
    public static final String ID = "JEE" ;
    
    @Override
    protected JEEQuestion constructQuestion( String fileName ) {
        return new JEEQuestion( fileName ) ;
    }

    @Override
    protected JComponent getSaveFileChooserAccessory() {
        String[] help = {
            "File name format :",
            "----------------------------",
            "1. [P|M|C]  - Subject code",
            "2. <int>    - Standard",
            "3. <String> - Book code",
            "   - PR > Pearson",
            "   - MR > MTG Reasoning",
            "4. <String> - Question type",
            "   - SCA, MCA, NT, LCT, ..",
            "5. [LCT#]",
            
            "----------- PR ------------",
            "6. Section",
            "   - VSAT > Very short answer",
            "   - SAT > Short answer",
            "   - ETQ > Essay type question",
            "   - CA_n > Concept application",
            "   - AT_n > Assessment type",
            "7. CA|AT section number",
            "7/8. Question number",
            
            "----------- MR ------------",
            "6. Question number"
        } ;
        return new SaveFileNameHelperAccessory( help ) ;
    }
}