package com.sandy.jnmaker.ui.dialogs.fib;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;

import org.apache.commons.lang.StringUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.EditMenu ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class FIBPanel extends FIBPanelUI implements ActionListener {

    private static final long serialVersionUID = -6630383705812553661L ;
    
    private static final String AC_FREEZE_TEXT   = "FREEZE_TEXT" ;
    private static final String AC_EXTRACT_BLANK = "EXTRACT_BLANK" ;
    
    private JPopupMenu popupMenu      = null ;
    private JMenuItem  freezeTextMI   = null ;
    private JMenuItem  extractBlankMI = null ;
    
    private List<String> blankTextList = new ArrayList<>() ;
    
    public FIBPanel( String selectedText ) {
        
        if( !selectedText.endsWith( "." ) ) {
            selectedText += "." ;
        }
        
        selectedText = StringUtils.capitalize( selectedText ) ;
        
        this.textArea.setText( selectedText ) ;
        UIUtil.associateEditMenu( this.textArea ) ;
        
        setUpPopupMenu() ;
        setUpListeners() ;
    }
    
    private void setUpPopupMenu() {
        
        freezeTextMI = new JMenuItem( "Freeze text" ) ;
        freezeTextMI.setActionCommand( AC_FREEZE_TEXT ) ;
        freezeTextMI.addActionListener( this ) ;
        
        extractBlankMI = new JMenuItem( "Extract blank" ) ;
        extractBlankMI.setActionCommand( AC_EXTRACT_BLANK ) ;
        extractBlankMI.addActionListener( this ) ;
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( freezeTextMI ) ;
        popupMenu.add( new EditMenu( popupMenu, textArea ) ) ;
    }
    
    private void setUpListeners() {
        
        textArea.addMouseListener( new MouseAdapter() {
            @Override 
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    popupMenu.show( textArea, e.getX(), e.getY() ) ;
                }
            }
        } );
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
        
        switch( e.getActionCommand() ) {
            
            case AC_FREEZE_TEXT:
                freezeText() ;
                break ;
            case AC_EXTRACT_BLANK:
                extractBlank() ;
                break ;
        }
    }
    
    private void freezeText() {
        
        textArea.setEditable( false ) ;
        popupMenu.remove( freezeTextMI ) ;
        popupMenu.add( extractBlankMI, 0 ) ;
    }
    
    private void extractBlank() {
        
        String selectedText = textArea.getSelectedText() ;
        if( StringUtil.isEmptyOrNull( selectedText ) ) {
            return ;
        }
        
        int startPosition = textArea.getSelectionStart() ;
        int endPosition   = textArea.getSelectionEnd() ;
        int curBlankNo    = blankTextList.size() ;
        
        blankTextList.add( selectedText ) ;
        
        String text = textArea.getText() ;
        StringBuilder newText = new StringBuilder() ;
        newText.append( text.subSequence( 0, startPosition ) )
               .append( "{" )
               .append( curBlankNo )
               .append( "}" )
               .append( text.subSequence( endPosition, text.length() ) ) ;
        
        textArea.setText( newText.toString() ) ;
        
        refreshPreview() ;
    }
    
    private void refreshPreview() {
        
        String previewText = "<html><body>" + textArea.getText() + "</body></html>" ;
        
        for( int i=0; i<blankTextList.size(); i++ ) {
            String blankTxt = blankTextList.get( i ) ;
            previewText = previewText.replace( "{"+i+"}", 
                                               "<b>" + blankTxt + "</b>" ) ;
        }
        
        previewLabel.setText( previewText ) ;
    }
}
