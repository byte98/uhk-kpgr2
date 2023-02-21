/*
 * Copyright (C) 2023 Jiri Skoda <jiri.skoda@uhk.cz>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.view;

import cz.uhk.fim.kpgr2.transforms.Col;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.MainWindowController;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Mutable;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableVertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Part;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Primitive;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Solid;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;

/**
 * Class representing main window of application
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class MainWindow extends JFrame
{
    /**
     * Default width of window
     */
    private static final int WINDOW_WIDTH = 1366;
    
    /**
     * Default height of window
     */
    private static final int WINDOW_HEIGHT = 768;
    
    /**
     * Default value for horizontal gap between components
     */
    private static final int H_GAP = 10;
    
    /**
     * Default value for vertical gap between components
     */
    private static final int V_GAP = 5;
    
    /**
     * Panel containing details
     */
    private JPanel detailsPanel;
    
    /**
     * Panel used to draw scene
     */
    private Panel panel;
    
    /**
     * Controller of window
     */
    private final MainWindowController controller;
    
    /**
     * Structure of scene
     */
    private JTree structure;
    
    /**
     * Split pane of details pane
     */
    private JSplitPane detailsSplit;
    
    /**
     * Main split pane
     */
    private JSplitPane contentSplit;
    
    /**
     * Scene which will be displayed
     */
    private Scene scene;
    
    /**
     * Panel containing properties
     */
    private JPanel propertiesPanel;
    
    /**
     * Creates new main window
     * @param controller Controller of window
     */
    public MainWindow(MainWindowController controller)
    {
        super("UHK FIM SKODAJI1: ZBUFFER");
        super.setIconImage(Icon.Z_BUFFER.toImageIcon().getImage());
        super.setLayout(new BorderLayout());
        super.setSize(new Dimension(MainWindow.WINDOW_WIDTH, MainWindow.WINDOW_HEIGHT));
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        this.controller = controller;
        this.initializeComponents();
    }
    
    /**
     * Initializes components of window
     */
    public void initializeComponents()
    {
        this.initializeToolBar();
        this.initializeDetailsPanel();
        this.panel = new Panel();
        this.contentSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.panel, this.detailsPanel);
        this.getContentPane().add(this.contentSplit, BorderLayout.CENTER);
        this.contentSplit.setDividerLocation(1000);
    }
    
    /**
     * Initializes details panel
     */
    private void initializeDetailsPanel()
    {
        this.detailsPanel = new JPanel();
        this.detailsPanel.setLayout(new BorderLayout());
        JLabel detailsHeader = new JLabel("Detaily");
        detailsHeader.setFont(detailsHeader.getFont().deriveFont(Font.BOLD));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEADING, MainWindow.H_GAP, MainWindow.V_GAP));
        header.add(new IconView(Icon.DETAIL));
        header.add(detailsHeader);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        this.detailsPanel.add(header, BorderLayout.NORTH);
        JPanel hierarchy = new JPanel(new BorderLayout());
        hierarchy.add(MainWindow.createIconLabel(Icon.TREE, "Struktura scény"), BorderLayout.NORTH);
        this.structure = new JTree(new TreeNode(Icon.TREE_SCENE, "Scéna: (žádná)", null));
        this.structure.setCellRenderer(new TreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
            {
                JPanel reti = new JPanel(new FlowLayout(FlowLayout.LEADING));
                reti.setSize(Integer.MAX_VALUE, 32);
                /*
                if (row > 0)
                {
                    TreePath tp = tree.getPathForRow(row);
                    if (Objects.nonNull(tp))
                    {
                        TreePath parent = tp.getParentPath();
                        if (Objects.nonNull(parent))
                        {
                            TreeNode parentNode = (TreeNode)parent.getLastPathComponent();
                            TreeNode last = (TreeNode)parentNode.getChildAt(parentNode.getChildCount() - 1);
                            if (value.equals(last))
                            {
                                //reti.add(new IconView(Icon.TREE_LEAF));
                                JLabel label = new JLabel("|");
                                reti.add(label);
                            }
                            else
                            {
                                //reti.add(new IconView(Icon.TREE_BRANCH));
                            }
                        }                        
                    }
                }
                */
                if (value instanceof TreeNode)
                {
                    TreeNode node = (TreeNode)value;
                    reti.add(new IconView(node.getIcon()));
                    reti.add(new JLabel(node.getText()));
                    reti.setOpaque(false);
                }
                else
                {
                    reti.add(new JLabel(value.toString()));
                }
                return reti;
            }
        });
        this.structure.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.structure.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                Object obj = MainWindow.this.structure.getLastSelectedPathComponent();
                MainWindow.this.propertiesPanel.removeAll();
                if (Objects.nonNull(obj) && obj instanceof TreeNode)
                {
                    TreeNode tn = (TreeNode)obj;
                    if (Objects.nonNull(tn) && Objects.nonNull(tn.getObject()))
                    {
                        MainWindow.this.showProperties(tn.getObject());
                    }                    
                }
                MainWindow.this.propertiesPanel.revalidate();
                MainWindow.this.propertiesPanel.repaint();
            }
            
        });
        hierarchy.add(new JScrollPane(this.structure), BorderLayout.CENTER);
        JPanel properties = new JPanel(new BorderLayout());
        properties.add(MainWindow.createIconLabel(Icon.PROPERTIES, "Vlastnosti objektu"), BorderLayout.NORTH);
        this.propertiesPanel = new JPanel();
        //properties.add(new JScrollPane(this.propertiesPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        properties.add(this.propertiesPanel);
        this.detailsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hierarchy, properties);
        this.detailsPanel.add(this.detailsSplit);
        this.detailsSplit.setDividerLocation(300);
    }
    
    /**
     * Displays scene
     * @param scene Scene which will be shown
     */
    public void setScene(Scene scene)
    {
        this.scene = scene;
        this.showStructure(scene);
    }
    
    /**
     * Shows structure of scene
     * @param scene Scene which structure will be shown
     */
    private void showStructure(Scene scene)
    {
        DefaultTreeModel model = (DefaultTreeModel)this.structure.getModel();
        TreeNode root = (TreeNode)model.getRoot();
        root.removeAllChildren();
        TreeNode newRoot = new TreeNode(Icon.TREE_SCENE, "Scéna: " + scene.getName(), scene);
        TreeNode camera = new TreeNode(Icon.TREE_CAMERA, "Kamera", scene.getCamera());
        TreeNode solids = new TreeNode(Icon.TREE_SOLIDS, "Tělesa (" + scene.getSolidsCount() + ")", null);
        for(Solid s: scene.getSolids())
        {
            TreeNode solid = new TreeNode(Icon.TREE_SOLID, s.getName(), s);
            TreeNode parts = new TreeNode(Icon.TREE_PARTS, "Části (" + s.getPartsCount() + ")", null);
            for(Part p: s.getParts())
            {
                TreeNode part = new TreeNode(Icon.TREE_PART, p.getName(), p);
                for(Primitive pm: p.getPrimitives())
                {
                    TreeNode primitive = new TreeNode(Icon.TREE_PRIMITIVE, pm.getName(), pm);
                    for(MutableVertex v: pm.getVertices())
                    {
                        TreeNode vertex = new TreeNode(Icon.TREE_VERTEX, v.getName(), v);
                        primitive.add(vertex);
                    }
                    part.add(primitive);
                }
                parts.add(part);
            }
            solid.add(parts);
            solids.add(solid);
        }
        newRoot.add(camera);
        newRoot.add(solids);
        model.setRoot(newRoot);
        model.reload();
    }
    
    /**
     * Shows properties of object
     * @param obj Object which properties will be shown
     */
    private void showProperties(Mutable obj)
    {
        String[] props = obj.getProperties();
        this.propertiesPanel.removeAll();
        //JPanel propertiesContent = new JPanel(new GridLayout(props.length, 2));
        JPanel propertiesContent = new JPanel();
        propertiesContent.setLayout(new BoxLayout(propertiesContent, BoxLayout.PAGE_AXIS));
        this.propertiesPanel.setLayout(new BorderLayout());
        for(String prop: props)
        {
            JComponent propName = new JPanel(new FlowLayout(FlowLayout.LEADING));
            // For known props create also an icon
            if (prop.toLowerCase().trim().equals("jméno") || prop.toLowerCase().trim().equals("název"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_TEXT, prop);
            }
            else if (prop.toLowerCase().trim().equals("x"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_X, prop);
            }
            else if (prop.toLowerCase().trim().equals("y"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_Y, prop);
            }
            else if (prop.toLowerCase().trim().equals("z"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_Z, prop);
            }
            else if (prop.toLowerCase().trim().equals("zenit") || prop.toLowerCase().trim().equals("azimut"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_ANGLE, prop);
            }
            else if (prop.toLowerCase().trim().equals("typ") || prop.toLowerCase().trim().equals("druh"))
            {
                propName = MainWindow.createIconLabel(Icon.TREE_TYPE, prop);
            }
            else
            {
                propName.add(new JLabel(prop));
            }
            JComponent propVal = null;
            if (obj.getType(prop) == String.class)
            {
                JTextField tf = new JTextField();
                tf.setText(obj.getString(prop));
                tf.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        obj.set(prop, tf.getText());
                    }                
                });
                if (obj.isMutable(prop) == false)
                {
                    tf.setEditable(false);
                }
                propVal = tf;
            }
            else if (obj.getType(prop) == Integer.class)
            {
                JSpinner spinner = new JSpinner();
                spinner.setModel(new SpinnerNumberModel(obj.getInt(prop), 0, Integer.MAX_VALUE, 1));
                spinner.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e)
                    {
                        obj.set(prop, (Integer)spinner.getValue());
                    }                    
                });
                if (obj.isMutable(prop) == false)
                {
                    ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
                }
                propVal = spinner;
            }
            else if (obj.getType(prop) == Double.class)
            {
                JSpinner spinner = new JSpinner();
                spinner.setModel(new SpinnerNumberModel(obj.getDouble(prop), 0, Double.MAX_VALUE, 0.1));
                spinner.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e)
                    {
                        obj.set(prop, (Double)spinner.getValue());
                    }                    
                });
                if (obj.isMutable(prop) == false)
                {
                    ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
                }
                propVal = spinner;
            }            
            String propType = "<neznámý>";
            Class propTypeVal = obj.getType(prop);
            if (propTypeVal == Integer.class)
            {
                propType = "celé číslo";
            }
            else if (propTypeVal == Double.class)
            {
                propType = "reálné číslo";
            }
            else if (propTypeVal == String.class)
            {
                propType = "řetězec";
            }
            else if (propTypeVal == Col.class)
            {
                propType = "barva";
            }
            propVal.setToolTipText(prop + " (" + propType + ")");
            JPanel propRow = new JPanel();
            propRow.setLayout(new GridLayout(1, 2));
            propRow.add(propName);
            propRow.add(propVal);
            propertiesContent.add(propRow);
        }                
        propertiesContent.add(Box.createRigidArea(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        this.propertiesPanel.add(propertiesContent, BorderLayout.CENTER);
    }
    
    /**
     * Initializes upper tool bar
     */
    private void initializeToolBar()
    {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEADING, MainWindow.H_GAP, MainWindow.V_GAP));
        toolBar.setFloatable(false);
        toolBar.add(MainWindow.createButton(Icon.LOAD, "Načíst", new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Path currentRelativePath = Paths.get("");
                JFileChooser fileChooser = new JFileChooser(currentRelativePath.toAbsolutePath().toString());
                fileChooser.setFileFilter(new FileNameExtensionFilter("Soubor JavaScript Object Notation", "json"));
                fileChooser.showOpenDialog(null);
                File f = fileChooser.getSelectedFile();
                if (Objects.nonNull(f))
                {
                    MainWindow.this.controller.loadClicked(f);
                }
            }        
        }));        
        toolBar.add(MainWindow.createButton(Icon.SAVE, "Uložit", new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
            }        
        }));
        toolBar.add(MainWindow.createButton(Icon.SAVE_AS, "Uložit jako", new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
            }        
        }));    
        JToggleButton detailsButton = MainWindow.createToggleButton(Icon.PANEL, "Detaily");
        detailsButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (detailsButton.isSelected())
                {
                    BorderLayout layout = (BorderLayout)MainWindow.this.getContentPane().getLayout();
                    MainWindow.this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.CENTER));
                    MainWindow.this.getContentPane().add(MainWindow.this.contentSplit, BorderLayout.CENTER);
                    MainWindow.this.contentSplit.removeAll();
                    MainWindow.this.contentSplit.add(MainWindow.this.panel, 0);
                    MainWindow.this.contentSplit.add(MainWindow.this.detailsPanel, 1);
                }
                else
                {
                    BorderLayout layout = (BorderLayout)MainWindow.this.getContentPane().getLayout();
                    MainWindow.this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.CENTER));
                    MainWindow.this.getContentPane().add(MainWindow.this.panel, BorderLayout.CENTER);
                }
                SwingUtilities.updateComponentTreeUI(MainWindow.this);
                MainWindow.this.contentSplit.setDividerLocation(0.7);
            }
        });
        toolBar.add(detailsButton);
        this.getContentPane().add(toolBar, BorderLayout.NORTH);
    }
    
    /**
     * Creates new button
     * @param icon Icon of button
     * @param text Text of button
     * @param listener Handler of button action
     * @return Button with icon, text and defined action handler
     */
    private static JButton createButton(Icon icon, String text, ActionListener listener)
    {
        JButton reti = new JButton();
        reti.setLayout(new FlowLayout(FlowLayout.CENTER, MainWindow.H_GAP, MainWindow.V_GAP));
        reti.add(new IconView(icon));
        reti.add(new JLabel(text));
        reti.addActionListener(listener);
        return reti;
    }
    
    /**
     * Creates new toggle button
     * @param icon Icon of button
     * @param text Text of button
     * @param listener Handler of button action
     * @return Toggle button with icon, text and defined action handler
     */
    private static JToggleButton createToggleButton(Icon icon, String text)
    {
        JToggleButton reti = new JToggleButton();
        reti.setLayout(new FlowLayout(FlowLayout.CENTER, MainWindow.H_GAP, MainWindow.V_GAP));
        reti.add(new IconView(icon));
        reti.add(new JLabel(text));
        reti.setSelected(true);
        return reti;
    }
    
    /**
     * Creates label with icon and text
     * @param icon Icon which will be displayed
     * @param text Text which will be displayed
     * @return Panel containing both icon and text
     */
    private static JPanel createIconLabel(Icon icon, String text)
    {
        JPanel reti = new JPanel(new FlowLayout(FlowLayout.LEADING, MainWindow.H_GAP, MainWindow.V_GAP));
        reti.add(new IconView(icon));
        reti.add(new JLabel(text));
        reti.setOpaque(false);
        return reti;
    }
}