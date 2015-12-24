package com.sandy.jnmaker.ui.dialogs;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.FlowLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.BorderFactory ;
import javax.swing.JButton ;
import javax.swing.JDialog ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.border.EtchedBorder ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.NoteType ;
import com.sandy.jnmaker.ui.dialogs.comment.CommentPanel ;
import com.sandy.jnmaker.ui.dialogs.definition.DefinitionPanel ;
import com.sandy.jnmaker.ui.dialogs.event.EventPanel ;
import com.sandy.jnmaker.ui.dialogs.fib.FIBPanel ;
import com.sandy.jnmaker.ui.dialogs.qa.QAPanel ;
import com.sandy.jnmaker.ui.dialogs.spellbee.SpellbeePanel ;
import com.sandy.jnmaker.ui.dialogs.truefalse.TFPanel ;
import com.sandy.jnmaker.ui.dialogs.wm.WMPanel ;
import com.sandy.jnmaker.ui.panels.JoveNotesPanel ;

public class NotesCreatorDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 981058739418516298L ;
    
    private static final String AC_OK     = "OK" ;
    private static final String AC_CANCEL = "CANCEL" ;
    
    private JLabel            titleLabel  = null ;
    private AbstractNotePanel centerPanel = null ;
    private JoveNotesPanel    jnPanel     = null ;

    public NotesCreatorDialog() {
        setUpUI() ;
    }
    
    public void createNote( String selectedText, NoteType noteType,
                            JoveNotesPanel jnPanel ) {
        
        this.jnPanel = jnPanel ;
        this.titleLabel.setText( getTitleText( noteType ) ) ;
        if( this.centerPanel != null ) {
            getContentPane().remove( this.centerPanel ) ;
        }
        this.centerPanel = getCenterPanel( selectedText, noteType ) ;
        getContentPane().add( this.centerPanel, BorderLayout.CENTER ) ;
        setVisible( true ) ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        if( e.getActionCommand().equals( AC_OK ) ) {
            okPressed() ;
        }
        else {
            setVisible( false ) ;
        }
    }
    
    public void okPressed() {
        String fmtNote = centerPanel.getFormattedNote() ;
        if( !StringUtil.isEmptyOrNull( fmtNote ) ) {
            fmtNote += "\n\n" ;
            this.jnPanel.addNote( fmtNote ) ;
            setVisible( false ) ;
        }
    }
    
    private void setUpUI() {
        
        Container contentPane = getContentPane() ;
        contentPane.setLayout( new BorderLayout() ) ;
        contentPane.add( getTitleLabel(), BorderLayout.NORTH ) ;
        contentPane.add( getButtonPanel(), BorderLayout.SOUTH ) ;
        
        super.setAlwaysOnTop( true ) ;
        
        SwingUtils.centerOnScreen( this, 600, 400 ) ;
    }
    
    private JLabel getTitleLabel() {
        
        titleLabel = new JLabel() ;
        titleLabel.setFont( new Font( "Tahoma", Font.BOLD, 15 ) );
        titleLabel.setForeground( Color.BLUE ) ;
        titleLabel.setBorder( BorderFactory.createCompoundBorder( 
                BorderFactory.createEtchedBorder( EtchedBorder.RAISED ), 
                BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) ) );
        return titleLabel ;
    }
    
    private JPanel getButtonPanel() {
        
        JPanel btnPanel = new JPanel() ;
        btnPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) ) ;
        btnPanel.add( createButton( "Cancel", AC_CANCEL ) ) ;
        btnPanel.add( createButton( "Ok",     AC_OK     ) ) ;
        return btnPanel ;
    }
    
    private JButton createButton( String label, String actionCmd ) {
        
        JButton button = new JButton( label ) ;
        button.setActionCommand( actionCmd ) ;
        button.addActionListener( this ) ;
        return button ;
    }
    
    private String getTitleText( NoteType noteType ) {
        
        String titleText = "" ;
        switch( noteType ) {
            case QA:
                titleText = "Make question answer (@qa) note." ;
                break ;
            case DEFINITION:
                titleText = "Make definition (@definition) note." ;
                break ;
            case FIB:
                titleText = "Make fill in the blanks (@fib) note." ;
                break ;
            case TRUE_FALSE:
                titleText = "Make true/false (@true_false) note." ;
                break ;
            case WORD_MEANING:
                titleText = "Make word meaning (@wm) note." ;
                break ;
            case SPELLBEE:
                titleText = "Make spellbee (@spellbee) note." ;
                break ;
            case COMMENT:
                titleText = "Make a comment note." ;
                break ;
            case EVENT:
                titleText = "Make event (@event) note." ;
                break ;
        }
        return titleText ;
    }

    private AbstractNotePanel getCenterPanel( String selectedText, NoteType noteType ) {
        
        AbstractNotePanel panel = null ;
        switch( noteType ) {
            case QA:
                panel = new QAPanel( selectedText ) ;
                break ;
            case FIB:
                panel = new FIBPanel( selectedText ) ;
                break ;
            case TRUE_FALSE:
                panel = new TFPanel( selectedText ) ;
                break ;
            case WORD_MEANING:
                panel = new WMPanel( selectedText ) ;
                break ;
            case SPELLBEE:
                panel = new SpellbeePanel( selectedText ) ;
                break ;
            case DEFINITION:
                panel = new DefinitionPanel( selectedText ) ;
                break ;
            case COMMENT:
                panel = new CommentPanel( selectedText ) ;
                break ;
            case EVENT:
                panel = new EventPanel( selectedText ) ;
                break ;
        }
        
        if( panel != null ) {
            panel.setParentDialog( this ) ;
        }
        
        return panel ;
    }
}
