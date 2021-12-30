package com.sandy.jnmaker.ui.panels.image;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED ;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Dimension ;
import java.awt.Font ;

import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

@SuppressWarnings( "serial" )
public class SaveFileNameHelperAccessory extends JPanel {
    
    private JTextArea textArea = null ;

    public SaveFileNameHelperAccessory( String[] helpContent ) {
        setUpUI() ;
        addHelpContent( helpContent ) ;
    }
    
    private void setUpUI() {
        
        setPreferredSize( new Dimension( 250, 300 ) ) ;
        setLayout( new BorderLayout() ) ;
        
        textArea = new JTextArea( 20, 40 ) ;
        textArea.setFont( new Font( "Courier New", Font.PLAIN, 11 ) ) ;
        textArea.setForeground( Color.BLACK ) ;
        textArea.setEditable( false ) ;
        
        JScrollPane sp = new JScrollPane( textArea ) ;
        sp.setHorizontalScrollBarPolicy( HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;
        sp.setVerticalScrollBarPolicy( VERTICAL_SCROLLBAR_AS_NEEDED ) ;
        
        add( sp, BorderLayout.CENTER ) ;
    }
    
    private void addHelpContent( String[] helpContent ) {
        StringBuilder sb = new StringBuilder() ;
        for( String content : helpContent ) {
            sb.append( content + "\n" ) ;
        }
        textArea.setText( sb.toString() ) ;
    }
}
