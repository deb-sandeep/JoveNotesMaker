package com.sandy.jnmaker.ui.menu.actions;

import javax.swing.AbstractAction ;
import javax.swing.Icon ;
import javax.swing.KeyStroke ;

import com.sandy.jnmaker.ui.helper.UIUtil ;

@SuppressWarnings( "serial" )
public abstract class AbstractBaseAction extends AbstractAction {
    
    private Icon smallIcon = null ;

    public AbstractBaseAction( String name ) {
        this( name, null, -1, -1, -1 ) ;
    }
    
    public AbstractBaseAction( String name, String iconName ) {
        this( name, iconName, -1, -1, -1 ) ;
    }
    
    public AbstractBaseAction( String name, int mnemonic, 
                               int accelerator, int accModifiers ) {
        this( name, null, mnemonic, accelerator, accModifiers ) ;
    }
    
    public Icon getSmallIcon() {
        return this.smallIcon ;
    }
    
    public AbstractBaseAction( String name, String iconName, 
                               int mnemonic, int accelerator, int accModifiers ) {
        super( name ) ;
        
        if( iconName != null ) {
            smallIcon = UIUtil.getIcon( iconName ) ;
            super.putValue( SMALL_ICON,     smallIcon ) ;
        }
        
        if( mnemonic != -1 ) {
            super.putValue( MNEMONIC_KEY, mnemonic ) ;
        }
        
        if( accelerator != -1 ) {
            KeyStroke ks = KeyStroke.getKeyStroke( accelerator, accModifiers ) ;
            super.putValue( ACCELERATOR_KEY, ks ) ;
        }
    }
}
