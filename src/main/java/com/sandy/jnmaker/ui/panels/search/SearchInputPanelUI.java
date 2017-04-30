package com.sandy.jnmaker.ui.panels.search;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Font ;
import java.awt.Insets ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

import javax.swing.JButton ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;
import javax.swing.JTextField ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.panels.common.JoveNotesTextPane ;

@SuppressWarnings( "serial" )
public abstract class SearchInputPanelUI extends JPanel 
    implements ActionListener {
    
    static final Logger log = Logger.getLogger( SearchInputPanelUI.class ) ;
    
    protected JTextField        queryTF     = null ;
    protected JButton           searchBtn   = null ;
    protected JoveNotesTextPane srcViewPane = null ;

    public SearchInputPanelUI() {
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
        
        this.queryTF   = getQueryTextField() ;
        this.searchBtn = getSearchButton() ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.add( queryTF, BorderLayout.CENTER ) ;
        panel.add( searchBtn, BorderLayout.EAST ) ;
        return panel ;
    }
    
    private JTextField getQueryTextField() {
        JTextField ta = new JTextField() ;
        ta.setFont( new Font( "Courier", Font.PLAIN, 12 ) ) ;
        ta.setBackground( Color.BLACK ) ;
        ta.setForeground( Color.YELLOW ) ; 
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
        JButton b = new JButton( "Search" ) ;
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

}
