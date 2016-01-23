package com.sandy.jnmaker.ui.notedialogs.fib;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.FocusEvent ;
import java.awt.event.FocusListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;

import org.apache.commons.lang.StringUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.PopupEditMenu ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class FIBPanel extends FIBPanelUI implements ActionListener {

    private static final long serialVersionUID = -6630383705812553661L ;
    
    private JPopupMenu popupMenu      = null ;
    private JMenuItem  freezeTextMI   = null ;
    private JMenuItem  extractBlankMI = null ;
    private JMenuItem  freezeTextAndExtractBlankMI = null ;
    
    private List<String> blankTextList = new ArrayList<>() ;
    
    public FIBPanel( String selectedText ) {
        
        if( StringUtil.isEmptyOrNull( selectedText ) ) {
            selectedText = "" ;
        }
        else {
            if( !selectedText.endsWith( "." ) ) {
                selectedText += "." ;
            }
            selectedText = StringUtils.capitalize( selectedText ) ;
        }
        
        this.textArea.setText( selectedText ) ;
        UIUtil.associateEditMenu( this.textArea ) ;
        
        setUpPopupMenu() ;
        setUpListeners() ;
    }
    
    private void setUpPopupMenu() {
        
        freezeTextMI = new JMenuItem( "Freeze text" ) ;
        freezeTextMI.addActionListener( this ) ;
        
        extractBlankMI = new JMenuItem( "Extract blank" ) ;
        extractBlankMI.addActionListener( this ) ;
        
        freezeTextAndExtractBlankMI = new JMenuItem( "Freeze and extract" ) ;
        freezeTextAndExtractBlankMI.addActionListener( this ) ;
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( freezeTextAndExtractBlankMI ) ;
        popupMenu.add( freezeTextMI ) ;
        popupMenu.add( new PopupEditMenu( popupMenu, textArea ) ) ;
    }
    
    private void setUpListeners() {
        
        super.bindOkPressEventCapture( textArea ) ;
        
        textArea.addMouseListener( new MouseAdapter() {
            
            @Override public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    popupMenu.show( textArea, e.getX(), e.getY() ) ;
                }
            }
        } ) ;
        
        textArea.addFocusListener(new FocusListener() {

            @Override public void focusGained(FocusEvent e) {
                 textArea.getCaret().setVisible( true ) ;
            }

            @Override public void focusLost( FocusEvent e ) {
                textArea.getCaret().setVisible( false ) ;
            }
        } ) ;
        
        textArea.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                handleKeyShortcutPressed( e.getModifiers(), e.getKeyCode() ) ;
            }
        } ) ;
    }
    
    protected void captureFocus() {
        this.textArea.requestFocus() ;
    }

    @Override
    public String getFormattedNote() {
        
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( "@fib \"" )
              .append( formatText( textArea.getText() ) )
              .append( "\"\n" ) ;
        
        for( int i=0; i<blankTextList.size(); i++ ) {
            String blankTxt = blankTextList.get( i ) ;
            buffer.append( "\"" )
                  .append( escapeQuotes( blankTxt ) ) 
                  .append( "\"" ) ;
            if( i != blankTextList.size()-1 ) {
                buffer.append( "\n" ) ;
            }
        }
        
        return buffer.toString() ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        if( e.getSource() == freezeTextMI ) {
            freezeText() ;
        }
        else if( e.getSource() == extractBlankMI ) {
            extractBlank() ;
        }
        else if( e.getSource() == freezeTextAndExtractBlankMI ) {
            freezeText() ;
            extractBlank() ;
        }
    }
    
    private void freezeText() {
        
        textArea.setEditable( false ) ;
        popupMenu.remove( freezeTextMI ) ;
        popupMenu.remove( freezeTextAndExtractBlankMI ) ;
        popupMenu.add( extractBlankMI, 0 ) ;
    }
    
    private void extractBlank() {
        
        String selectedText = textArea.getSelectedText() ;
        if( StringUtil.isEmptyOrNull( selectedText ) ) {
            return ;
        }
        
        int curBlankNo    = blankTextList.size() ;
        blankTextList.add( selectedText ) ;
        textArea.replaceSelection( "{" + curBlankNo + "}" ) ;
        refreshPreview() ;
    }
    
    private void refreshPreview() {
        
        String previewText = "<html><body>" + textArea.getText() + "</body></html>" ;
        
        previewText = previewText.replaceAll( "\n\n", "<p>" ) ;
        previewText = previewText.replaceAll( "\n", "<br>" ) ;
        
        for( int i=0; i<blankTextList.size(); i++ ) {
            String blankTxt = blankTextList.get( i ) ;
            previewText = previewText.replace( "{"+i+"}", 
                                               "<b>" + blankTxt + "</b>" ) ;
        }
        
        previewLabel.setText( previewText ) ;
    }
    
    private void handleKeyShortcutPressed( int mod, int code ) {
        
        if( mod == KeyEvent.CTRL_MASK ) {
            switch( code ) {
                case KeyEvent.VK_F:
                    freezeText() ;
                    break ;
                case KeyEvent.VK_E:
                    extractBlank() ;
                    break ;
            }
        }
    }
}
