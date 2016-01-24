package com.sandy.jnmaker.tools;

import java.awt.Dimension ;
import java.util.List ;

import javax.swing.JOptionPane ;
import javax.swing.JPanel ;

public abstract class AbstractToolConfigPanel extends JPanel {

    private static final long serialVersionUID = -2589969430412971534L ;
    
    private String panelDisplayName = null ;
    private ToolConfigDialog parentDialog = null ;
    
    public AbstractToolConfigPanel( String displayName ) {
        this.panelDisplayName = displayName ;
    }
    
    public String getPanelDisplayName() {
        return this.panelDisplayName ;
    }
    
    public void setParentDialog( ToolConfigDialog dialog ) {
        this.parentDialog = dialog ;
    }
    
    public ToolConfigDialog getParentDialog() {
        return this.parentDialog ;
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
    
    /**
     * This method will be called after the user presses the OK button on the 
     * configuration dialog. Subclasses should override this method to validate
     * the user input and return either null or a list with zero elements in 
     * case the user input is valid.
     * 
     * A list with more than one messages can be returned, in case of input failure.
     * In such case, the dialog will show the validation messages and not call
     * the executeTool method on the tool.
     */
    protected abstract List<String> validateUserInput() ;
}
