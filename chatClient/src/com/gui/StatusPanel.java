package com.gui;

import com.chatApp.Client;
import com.chatApp.UserStatusListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class StatusPanel extends JPanel implements UserStatusListener {
    private Client client;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> userList;


    public StatusPanel(Client client){
        this.client = client;
        client.addStatusListener(this);

        Dimension dim = getPreferredSize();
        dim.width = 250;
        setPreferredSize(dim);

        //components
        userList = new JList<>(listModel);

        //set borders for this panel
        Border inner = BorderFactory.createTitledBorder("Users Online");
        Border outer = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outer, inner));

        setLayout(new BorderLayout());
        add(userList, BorderLayout.CENTER);
    }

    @Override
    public void online(String user) {
        listModel.add(0, user);
    }

    @Override
    public void offline(String user) {
        listModel.removeElement(user);
    }
}
