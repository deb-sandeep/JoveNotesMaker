package com.sandy.jnmaker.ui.helper;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class LargeCaret extends DefaultCaret {

    private static final long serialVersionUID = 1L ;
    
    public LargeCaret() {
        setBlinkRate( 500 );
    }

    protected synchronized void damage( Rectangle r ) {
        
        if( r == null ) return ;
        x = r.x;
        y = r.y + ( r.height * 4 / 5 - 3 ) ;
        width = 5 ;
        height = 10 ;
        repaint() ;
    }

    @SuppressWarnings( "deprecation" )
    public void paint( Graphics g ) {
        
        JTextComponent comp = getComponent() ;
        if( comp == null ) return ;

        int dot = getDot() ;
        Rectangle r = null ;
        try {
            r = comp.modelToView( dot ) ;
        }
        catch( BadLocationException e ) {
            return ;
        }
        if( r == null ) return ;

        if( ( x != r.x ) || ( y != r.y ) ) {
            repaint() ;
            x = r.x ;
            y = r.y ;
            width = 5 ;
            height = 10 ;
        }

        if( isVisible() ) {
            g.setColor( comp.getCaretColor() ) ;
            g.fillRect( r.x, r.y, 5, 10 ) ;
        }
    }
}