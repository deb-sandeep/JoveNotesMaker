package com.sandy.jnmaker.ui.helper;

import static com.sandy.jnmaker.util.ObjectRepository.getApp ;

import java.awt.Dimension ;
import java.awt.Insets ;
import java.awt.event.ActionListener ;

import javax.swing.ImageIcon ;
import javax.swing.JButton ;

import com.sandy.common.ui.SwingUtils ;

public class UIUtil {

    public static ImageIcon getIcon( String iconName ) {
        return SwingUtils.getIcon( getApp().getClass(), iconName ) ;
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
}
