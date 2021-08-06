package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class ChatToolBar extends JPanel {

    private Client client;
    private JLabel headLabel;

    public ChatToolBar(Client client){
        this.client = client;
        headLabel = new JLabel("");

        //set borders for this panel
        Border inner = BorderFactory.createEtchedBorder();
        Border outer = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outer, inner));

        //set Layout
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(headLabel);
    }

    public void setHeadLabelText(String text) {
        headLabel.setText(text);
    }
    public String getHeadLabelText(){
        return headLabel.getText();
    }
}
