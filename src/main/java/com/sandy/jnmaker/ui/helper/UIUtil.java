package com.sandy.jnmaker.ui.helper;

import static com.sandy.jnmaker.util.ObjectRepository.getApp ;

import java.awt.Color ;
import java.awt.Dimension ;
import java.awt.Insets ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;

import javax.swing.ImageIcon ;
import javax.swing.JButton ;
import javax.swing.JComponent ;
import javax.swing.JPanel ;
import javax.swing.JPopupMenu ;
import javax.swing.JScrollBar ;
import javax.swing.JSplitPane ;
import javax.swing.JTextPane ;
import javax.swing.UIDefaults ;
import javax.swing.text.JTextComponent ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.jnmaker.ui.actions.AbstractBaseAction ;

public class UIUtil {

    public static final Color EDITOR_BG_COLOR = new Color( 57, 56, 46 ) ; 
    public static final Color STRING_COLOR    = new Color( 229, 237, 220 ) ;
    public static final Color KEYWORD_COLOR   = new Color( 86, 210, 211 ) ;
    public static final Color NUMBER_COLOR    = Color.RED ;
    
    public static ImageIcon getIcon( String iconName ) {
        return SwingUtils.getIcon( getApp().getClass(), iconName ) ;
    }
    
    public static JButton getActionBtn( AbstractBaseAction action ) {
        
        JButton button = new JButton() ;
        
        button.setIcon( action.getSmallIcon() ) ;
        button.setMargin( new Insets( 0, 0, 0, 0 ) ) ;
        button.setBorderPainted( false ) ;
        button.setFocusPainted( true ) ;
        button.setIconTextGap( 0 ) ;
        button.setPreferredSize( new Dimension( 30, 30 ) );
        button.setAction( action ) ;
        button.setText( null ) ;
        
        return button ;
    }
    
    public static JButton getActionBtn( String iconName, String actionCmd, 
                                        ActionListener listener ) {
        
        JButton button = new JButton() ;
        
        button.setIcon( getIcon( iconName ) ) ;
        button.setMargin( new Insets( 0, 0, 0, 0 ) ) ;
        button.setBorderPainted( false ) ;
        button.setFocusPainted( true ) ;
        button.setIconTextGap( 0 ) ;
        button.setPreferredSize( new Dimension( 30, 30 ) );
        button.setActionCommand( actionCmd ) ;
        button.addActionListener( listener ) ;
        
        return button ;
    }
    
    public static void setPanelBackground( Color bgColor, JPanel panel ) {
        
        UIDefaults defaults = new UIDefaults();
        defaults.put( "Panel.background", bgColor ) ;
        setNibusOverridesProperty( panel, defaults ) ;
        panel.setBackground( bgColor ) ;
    }
    
    public static void setTextPaneBackground( Color bgColor, JTextPane textPane ) {
        
        UIDefaults defaults = new UIDefaults();
        defaults.put( "TextPane[Enabled].backgroundPainter", bgColor ) ;
        defaults.put( "TextPane[Disabled].backgroundPainter", bgColor ) ;
        setNibusOverridesProperty( textPane, defaults ) ;
        textPane.setBackground( bgColor ) ;
    }
    
    public static void setScrollBarBackground( Color bgColor, JScrollBar sb ) {
        
        UIDefaults defaults = new UIDefaults();
        defaults.put( "ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", bgColor ) ;
        setNibusOverridesProperty( sb, defaults ) ;
        sb.setBackground( bgColor ) ;
    }
    
    public static void setSplitPaneBackground( Color bgColor, JSplitPane sp ) {
        
        UIDefaults defaults = new UIDefaults();
        defaults.put( "SplitPane:SplitPaneDivider[Enabled].backgroundPainter", bgColor ) ;
        setNibusOverridesProperty( sp, defaults ) ;
        sp.setBackground( bgColor ) ;
    }
    
    private static void setNibusOverridesProperty( JComponent comp, UIDefaults uid ) {
        
        comp.putClientProperty( "Nimbus.Overrides", uid ) ;
        comp.putClientProperty( "Nimbus.Overrides.InheritDefaults", true ) ;
    }
    
    public static PopupEditMenu associateEditMenu( final JTextComponent textComp ) {
        
        final JPopupMenu popup = new JPopupMenu() ;
        PopupEditMenu editMenu = new PopupEditMenu( popup, textComp ) ; 
        popup.add( editMenu ) ;
        
        textComp.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    popup.show( textComp, e.getX(), e.getY() ) ;
                }
            }
        } ) ;
        
        return editMenu ;
    }
}
