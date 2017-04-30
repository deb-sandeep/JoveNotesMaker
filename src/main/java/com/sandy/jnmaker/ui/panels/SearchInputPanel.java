package com.sandy.jnmaker.ui.panels;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Font ;
import java.awt.Insets ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

import javax.swing.JButton ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;
import javax.swing.JTextArea ;

import org.apache.log4j.Logger ;

@SuppressWarnings( "serial" )
public class SearchInputPanel extends JPanel 
    implements ActionListener {
    
    private static final Logger log = Logger.getLogger( SearchInputPanel.class ) ;
    
    private JTextArea         queryTA     = null ;
    private JButton           searchBtn   = null ;
    private JoveNotesTextPane srcViewPane = null ;

    public SearchInputPanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( getQueryAndNavPanel(), BorderLayout.NORTH ) ;
        add( getResultAndDetailPanel(), BorderLayout.CENTER ) ;
    }

    private Component getQueryAndNavPanel() {
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.add( getQueryPanel(), BorderLayout.CENTER ) ;
        panel.add( getNavPanel(), BorderLayout.SOUTH ) ;
        return panel ;
    }

    private Component getResultAndDetailPanel() {
        JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        sp.setDividerLocation( 0.5D ) ;
        sp.add( getResultTablePanel() ) ;
        sp.add( getSourceViewerPanel() ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.add( sp, BorderLayout.CENTER ) ;
        return panel ;
    }

    private Component getNavPanel() {
        JPanel panel = new JPanel( new BorderLayout() ) ;
        return panel ;
    }

    private Component getQueryPanel() {
        
        this.queryTA = getQueryTextArea() ;
        this.searchBtn = getSearchButton() ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.add( queryTA, BorderLayout.CENTER ) ;
        panel.add( searchBtn, BorderLayout.EAST ) ;
        return panel ;
    }
    
    private JTextArea getQueryTextArea() {
        JTextArea ta = new JTextArea() ;
        ta.setRows( 2 ) ;
        ta.setFont( new Font( "Courier", Font.PLAIN, 12 ) ) ;
        ta.setBackground( Color.BLACK ) ;
        ta.setForeground( Color.YELLOW ) ; 
        ta.setWrapStyleWord( true ) ;
        ta.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    actionPerformed( null ) ;
                }
            }
        } ) ; 
        return ta ;
    }
    
    private JButton getSearchButton() {
        JButton b = new JButton() ;
        b.setMargin( new Insets( 0, 0, 0, 0 ) ) ;
        b.setBorderPainted( false ) ;
        b.setFocusPainted( true ) ;
        b.setIconTextGap( 0 ) ;
        b.addActionListener( this ) ;
        return b ;
    }

    private Component getSourceViewerPanel() {
        this.srcViewPane = new JoveNotesTextPane() ;
        return this.srcViewPane ;
    }

    private Component getResultTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() ) ;
        return panel ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        log.debug( "Process query" ) ;
    }
}
