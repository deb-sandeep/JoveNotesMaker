package com.sandy.jnmaker.poc;

import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JScrollPane ;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sandy.common.ui.SwingUtils ;

/**
 * http://stackoverflow.com/questions/19435181/how-to-set-default-background-color-for-jtextpane
 */
public class BlackTextPanePOC {
  public static void main(String... args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SwingUtils.setNimbusLookAndFeel() ;
        JFrame frame = new JFrame("Example setting background color on JTextPane");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.add(blackJTextPane());
        frame.setSize(800, 600);
        frame.setVisible(true);
      }

      private Component blackJTextPane() {
        JTextPane pane = new JTextPane();
        pane.setBackground(Color.BLACK);
        pane.setForeground(Color.WHITE);
        pane.setText("Here is example text");
        return new JScrollPane(pane) ;
      }
    });
  }
}
