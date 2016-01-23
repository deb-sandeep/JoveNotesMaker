package com.sandy.jnmaker.tools;

import java.awt.Dimension ;

import javax.swing.JOptionPane ;
import javax.swing.JPanel ;

public abstract class AbstractToolConfigPanel extends JPanel {

    private static final long serialVersionUID = -2589969430412971534L ;
    
    private String panelDisplayName = null ;
    
    public AbstractToolConfigPanel( String displayName ) {
        this.panelDisplayName = displayName ;
    }
    
    public String getPanelDisplayName() {
        return this.panelDisplayName ;
    }
    
    protected void showErrorMsg( String msg ) {
        JOptionPane.showMessageDialog( this, msg, "Input error", 
                                       JOptionPane.ERROR_MESSAGE ) ; 
    }

    protected void captureFocus() {
        // Do nothing by default
    }
    
    protected Dimension getPreferredDialogSize() {
        return new Dimension( 600, 450 ) ;
    }
}
