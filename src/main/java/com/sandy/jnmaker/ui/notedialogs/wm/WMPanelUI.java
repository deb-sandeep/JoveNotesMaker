package com.sandy.jnmaker.ui.notedialogs.wm ;

import com.sandy.jnmaker.ui.notedialogs.AbstractNotePanel ;

public abstract class WMPanelUI extends AbstractNotePanel {

    private static final long serialVersionUID = 9015449326144115519L;
 
    public WMPanelUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        javax.swing.JLabel wordLabel = new javax.swing.JLabel();
        wordTF = new javax.swing.JTextField();
        getMeaningBtn = new javax.swing.JButton();
        meaningLabel = new javax.swing.JLabel();
        javax.swing.JScrollPane sp1 = new javax.swing.JScrollPane();
        meaningTF = new javax.swing.JTextArea();
        msgLabel = new javax.swing.JLabel();
        javax.swing.JLabel pronunciationLabel = new javax.swing.JLabel();
        pronunciationTF = new javax.swing.JTextField();

        setBackground(java.awt.Color.lightGray);

        wordLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        wordLabel.setText("Word");

        wordTF.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        getMeaningBtn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        getMeaningBtn.setText("Get meaning");

        meaningLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        meaningLabel.setText("Meaning");

        meaningTF.setColumns(20);
        meaningTF.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        meaningTF.setLineWrap(true);
        meaningTF.setRows(5);
        meaningTF.setTabSize(4);
        meaningTF.setWrapStyleWord(true);
        sp1.setViewportView(meaningTF);

        msgLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        msgLabel.setForeground(new java.awt.Color(39, 121, 241));

        pronunciationLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        pronunciationLabel.setText("Pronunciation");

        pronunciationTF.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp1)
                    .addComponent(msgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(meaningLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pronunciationLabel)
                            .addComponent(wordLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(wordTF, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getMeaningBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                            .addComponent(pronunciationTF))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wordLabel)
                    .addComponent(wordTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getMeaningBtn))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pronunciationLabel)
                    .addComponent(pronunciationTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(meaningLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(msgLabel)
                .addContainerGap())
        );
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    protected javax.swing.JButton getMeaningBtn;
    protected javax.swing.JLabel meaningLabel;
    protected javax.swing.JTextArea meaningTF;
    protected javax.swing.JLabel msgLabel;
    protected javax.swing.JTextField pronunciationTF;
    protected javax.swing.JTextField wordTF;
    // End of variables declaration                   
}