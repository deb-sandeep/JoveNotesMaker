package com.sandy.jnmaker.tools;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.FlowLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyEvent ;
import java.awt.event.WindowEvent ;
import java.awt.event.WindowFocusListener ;
import java.util.List ;

import javax.swing.BorderFactory ;
import javax.swing.JButton ;
import javax.swing.JComponent ;
import javax.swing.JDialog ;
import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.KeyStroke ;
import javax.swing.border.EtchedBorder ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.SwingUtils ;

public class ToolConfigDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1 ;
    static final Logger logger = Logger.getLogger( ToolConfigDialog.class ) ;
    
    private static final String AC_OK     = "OK" ;
    private static final String AC_CANCEL = "CANCEL" ;
    
    public static enum UserChoice { OK, CANCEL } ;
    
    private JLabel                  titleLabel  = null ;
    private AbstractToolConfigPanel centerPanel = null ;
    
    private UserChoice userChoice = UserChoice.CANCEL ;

    public ToolConfigDialog( AbstractToolConfigPanel centerPanel ) {
        this.centerPanel = centerPanel ;
        this.centerPanel.setParentDialog( this ) ;
        setUpUI( centerPanel ) ;
        setUpListeners() ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( AC_OK ) ) {
            userChoice = UserChoice.OK ;
            if( userChoice == UserChoice.OK ) {
                List<String> errorMsgs = centerPanel.validateUserInput() ;
                if( errorMsgs != null && !errorMsgs.isEmpty() ) {
                    showErrorMessages( "Validation errors", errorMsgs ) ;
                    return ;
                }
            }
        }
        else {
            userChoice = UserChoice.CANCEL ;
        }
        setVisible( false ) ;
    }
    
    public UserChoice showDialog() {
        // This dialog being modal, the setVisible method will not return till
        // the setVisible( false ) method has been called.
        super.setVisible( true ) ;
        return userChoice ;
    }
    
    private void setUpUI( AbstractToolConfigPanel centerPanel ) {
        
        Container contentPane = getContentPane() ;
        contentPane.setLayout( new BorderLayout() ) ;
        contentPane.add( getTitleLabel( centerPanel.getPanelDisplayName() ), 
                         BorderLayout.NORTH ) ;
        contentPane.add( getButtonPanel(), BorderLayout.SOUTH ) ;
        contentPane.add( centerPanel, BorderLayout.CENTER ) ;
        
        super.setModalityType( ModalityType.APPLICATION_MODAL ) ;
        
        SwingUtils.centerOnScreen( this, 
                                   centerPanel.getPreferredDialogSize().width,
                                   centerPanel.getPreferredDialogSize().height ) ;
    }
    
    private void setUpListeners() {
        
        addWindowFocusListener() ;
        addEscapeListener() ;
    }
    
    private void addWindowFocusListener() {
        
        addWindowFocusListener( new WindowFocusListener() {
            @Override public void windowLostFocus( WindowEvent e ) { }
            @Override public void windowGainedFocus( WindowEvent e ) {
                centerPanel.captureFocus() ;
            }
        } );
    }
    
    private void addEscapeListener() {
        
        ActionListener escListener = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                userChoice = UserChoice.CANCEL ;
                ToolConfigDialog.this.setVisible(false);
            }
        } ;
        ToolConfigDialog.this.getRootPane().registerKeyboardAction(
                escListener,
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_IN_FOCUSED_WINDOW ) ;
    }    
    
    private JLabel getTitleLabel( String title ) {
        
        titleLabel = new JLabel() ;
        titleLabel.setFont( new Font( "Tahoma", Font.BOLD, 15 ) );
        titleLabel.setForeground( Color.BLUE ) ;
        titleLabel.setBorder( BorderFactory.createCompoundBorder( 
                BorderFactory.createEtchedBorder( EtchedBorder.RAISED ), 
                BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) ) );
        titleLabel.setText( title ) ; 
        
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

    public void showErrorMessages( String title, List<String> errorMsgs ) {
        StringBuilder buffer = new StringBuilder( "<html><body><ul>" ) ;
        for( String msg : errorMsgs ) {
            buffer.append( "<li>" ).append( msg ).append( "</li>" ) ;
        }
        buffer.append( "</ul></body></html>" ) ;
        
        JOptionPane.showMessageDialog( this, buffer.toString(), title,
                                       JOptionPane.ERROR_MESSAGE ) ;
    }
}
