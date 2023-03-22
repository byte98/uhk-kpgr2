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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Component representing field which can change colour
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class ColorField extends JPanel implements ActionListener
{
    /**
     * Colour which is actually displayed
     */
    private Col colour;
    
    /**
     * Button which opens colour dialogue
     */
    private JButton button;
    
    /**
     * Text field containing textual representation of text field
     */
    private JTextField textField;
    
    /**
     * List of listeners of colour change
     */
    private final List<ActionListener> actionListeners;
    
    /**
     * Creates new field with colour settings
     * @param colour Colour which will be displayed
     */
    public ColorField(Col colour)
    {
        this.colour = colour;
        this.actionListeners = new ArrayList<>();
        this.initializeComponents();
        this.initializeValues();
    }
    
    /**
     * Sets actually displayed colour
     * @param colour New colour which will be displayed
     */
    public void setColour(Col colour)
    {
        this.colour = colour;
        this.initializeValues();
    }
    
    /**
     * Gets actual colour set to colour field
     * @return Actual colour
     */
    public Col getColour()
    {
        return this.colour;
    }
    
    /**
     * Initializes all internal components
     */
    private void initializeComponents()
    {
        this.textField = new JTextField();
        super.setBorder(this.textField.getBorder());
        super.setBackground(this.textField.getBackground());
        JPanel tfWrapper = new JPanel();
        tfWrapper.setLayout(new GridBagLayout());
        this.textField.setBorder(BorderFactory.createEmptyBorder());
        this.textField.setLayout(new BoxLayout(this.textField, BoxLayout.LINE_AXIS));
        this.textField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, this.textField.getFont().getSize()));
        this.textField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String text = ColorField.this.textField.getText();
                if (text.length() == 6)
                {
                    String r = new StringBuffer().append(text.charAt(0)).append(text.charAt(1)).toString();
                    String g = new StringBuffer().append(text.charAt(2)).append(text.charAt(3)).toString();
                    String b = new StringBuffer().append(text.charAt(4)).append(text.charAt(5)).toString();
                    int ri, gi, bi;
                    try
                    {
                        ri = Integer.parseInt(r, 16);
                        gi = Integer.parseInt(g, 16);
                        bi = Integer.parseInt(b, 16);
                        ColorField.this.colour = new Col(ri, gi, bi);
                        ColorField.this.invokeAction();
                    }
                    catch (NumberFormatException ex){}
                }
                ColorField.this.initializeValues();
            }        
        });
        tfWrapper.add(this.textField);
        this.button = new JButton();
        this.button.setBorder(null);
        this.button.addActionListener(this);
        super.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        super.add(tfWrapper);
        super.add(this.button);
        
    }
    
    /**
     * Initializes values of internal components
     */
    private void initializeValues()
    {
        this.textField.setText(this.getColourString());
        JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(16, 16));
        colorPanel.setBackground(new Color((float)this.colour.r, (float)this.colour.g, (float)this.colour.b));
        this.button.removeAll();
        this.button.add(colorPanel);
        this.button.revalidate();
        this.button.repaint();
    }
    
    /**
     * Gets string representation of colour
     * @return String representation of colour
     */
    private String getColourString()
    {
        return String.format("%02x%02x%02x", (int)(this.colour.r * 255f), (int)(this.colour.g * 255f), (int)(this.colour.b * 255f));
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        Color c = JColorChooser.showDialog(this, "Vyberte barvu", new Color(this.colour.getRGB()));
        if (Objects.nonNull(c))
        {
            this.colour = new Col(c.getRed(), c.getGreen(), c.getBlue());
            this.initializeValues();
            this.invokeAction();
        }
    }
    
    /**
     * Informs all action listeners about performed action
     */
    private void invokeAction()
    {
        for(ActionListener al: this.actionListeners)
        {
            al.actionPerformed(null);
        }
    }
    
    /**
     * Adds listener to change of colour
     * @param actionListener Action listener which will be added
     */
    public void addActionListener(ActionListener actionListener)
    {
        this.actionListeners.add(actionListener);
    }
}
