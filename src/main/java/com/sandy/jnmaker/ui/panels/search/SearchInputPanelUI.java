package com.sandy.jnmaker.ui.panels.search;

import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_CHAPTER_NAME ;
import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_CHAPTER_NO ;
import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_CONTENT ;
import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_NOTE_TYPE ;
import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_SUBJECT ;
import static com.sandy.jnmaker.ui.panels.search.SearchResultsTableModel.COL_SYLLABUS ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Font ;
import java.awt.Insets ;
import java.awt.event.ActionListener ;

import javax.swing.JButton ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JSplitPane ;
import javax.swing.JTable ;
import javax.swing.JTextField ;
import javax.swing.ListSelectionModel ;
import javax.swing.event.ListSelectionListener ;
import javax.swing.table.TableColumn ;
import javax.swing.table.TableColumnModel ;
import javax.swing.table.TableRowSorter ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.panels.common.JoveNotesTextPane ;

@SuppressWarnings( "serial" )
public abstract class SearchInputPanelUI extends JPanel 
    implements ActionListener, ListSelectionListener {
    
    static final Logger log = Logger.getLogger( SearchInputPanelUI.class ) ;
    
    private static final Font TABLE_FONT = new Font( "Helvetica", Font.PLAIN, 11 ) ;
    
    protected JTextField        queryTF     = null ;
    protected JButton           searchBtn   = null ;
    protected JButton           moveSelectedBtn= null ;
    protected JoveNotesTextPane jnSrcPane = null ;
    protected JTable            resultsTable= null ;
    
    protected SearchResultsTableModel               tableModel = null ;
    private TableRowSorter<SearchResultsTableModel> sorter     = null ;
    

    public SearchInputPanelUI() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( getQueryAndMovePanel(), BorderLayout.NORTH ) ;
        add( getResultAndDetailPanel(), BorderLayout.CENTER ) ;
    }

    private Component getQueryAndMovePanel() {
        this.queryTF         = getQueryTextField() ;
        this.searchBtn       = getSearchButton() ;
        this.moveSelectedBtn = getMoveSelectedButton() ;
        
        JPanel btnPanel = new JPanel() ;
        btnPanel.add( searchBtn ) ;
        btnPanel.add( moveSelectedBtn ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.add( queryTF, BorderLayout.CENTER ) ;
        panel.add( btnPanel, BorderLayout.EAST ) ;
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

    private JTextField getQueryTextField() {
        JTextField ta = new JTextField() ;
        ta.setFont( new Font( "Courier", Font.PLAIN, 13 ) ) ;
        ta.setBackground( Color.BLACK ) ;
        ta.setForeground( Color.YELLOW ) ; 
        ta.addActionListener( this ) ;
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
    
    private JButton getMoveSelectedButton() {
        JButton b = new JButton( ">>" ) ;
        b.setMargin( new Insets( 0, 0, 0, 0 ) ) ;
        b.setBorderPainted( false ) ;
        b.setFocusPainted( true ) ;
        b.setIconTextGap( 0 ) ;
        b.addActionListener( this ) ;
        return b ;
    }

    private Component getSourceViewerPanel() {
        
        jnSrcPane = new JoveNotesTextPane() ;
        jnSrcPane.setFont( new Font( "Courier", Font.PLAIN, 11 ) ) ;        
        
        JScrollPane sp = new JScrollPane( jnSrcPane, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        
        UIUtil.setScrollBarBackground( UIUtil.EDITOR_BG_COLOR, 
                                       sp.getVerticalScrollBar() ) ;
        
        return sp ;
    }

    private Component getResultTablePanel() {
        
        tableModel = new SearchResultsTableModel() ;
        resultsTable = new JTable( tableModel ) ;
        sorter = new TableRowSorter<SearchResultsTableModel>( tableModel ) ;
        

        JScrollPane tableSP = new JScrollPane( resultsTable ) ;
        tableSP.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;

        // Add the table model, table column model etc for the table.
        resultsTable.setAutoCreateRowSorter( true ) ;
        
        resultsTable.setFont( TABLE_FONT ) ;
        resultsTable.getTableHeader().setFont( TABLE_FONT ) ;
        resultsTable.setRowHeight( 15 ) ;
        resultsTable.setDoubleBuffered( true ) ;
        resultsTable.setRowSorter( this.sorter ) ;
        resultsTable.setRowSelectionAllowed( true ) ;
        resultsTable.setColumnSelectionAllowed( false ) ;
        resultsTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ) ;
        resultsTable.getSelectionModel().addListSelectionListener( this ) ;
        
        sorter.setRowFilter( null ) ;

        setColumnProperties( COL_SYLLABUS,     45 ) ;
        setColumnProperties( COL_SUBJECT,      50 ) ;
        setColumnProperties( COL_CHAPTER_NO,   15 ) ;
        setColumnProperties( COL_CHAPTER_NAME, 100 ) ;
        setColumnProperties( COL_NOTE_TYPE,    50 ) ;
        setColumnProperties( COL_CONTENT,      200 ) ;
        
        return tableSP ;
    }

    /**
     * A tiny helper method to set the properties of the columns in the ITD
     * table.
     *
     * @param colId The identifier of the column
     * @param width The preferred width
     */
    private void setColumnProperties( final int colId, final int width ) {
        final TableColumnModel colModel = resultsTable.getColumnModel() ;
        final TableColumn col = colModel.getColumn( colId ) ;
        col.setPreferredWidth( width ) ;
        col.setMinWidth( width ) ;
        col.setResizable( true ) ;
    }
}
