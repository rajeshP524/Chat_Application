package com.gui;

import com.chatApp.Client;
import com.chatApp.UserStatusListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class StatusPanel extends JPanel implements UserStatusListener {
    private Client client;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> userList;

    private StatusPanelListener statusPanelListener;


    public StatusPanel(Client client){
        this.client = client;
        client.addStatusListener(this);

        Dimension dim = getPreferredSize();
        dim.width = 130;
        setPreferredSize(dim);

        //components
        userList = new JList<>(listModel);

        //set borders for this panel
        Border inner = BorderFactory.createTitledBorder("Users Online");
        Border outer = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outer, inner));

        setLayout(new BorderLayout());
        add(new JScrollPane(userList), BorderLayout.CENTER);

        //setting listener
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1){
                    String user = userList.getSelectedValue();
                    if(statusPanelListener != null){
                        statusPanelListener.actionPerformed(user);
                    }

                }
            }
        });
    }

    @Override
    public void online(String user) {
        listModel.add(0, user);
    }

    @Override
    public void offline(String user) {
        listModel.removeElement(user);
    }

    public StatusPanelListener getStatusPanelListener() {
        return statusPanelListener;
    }

    public void setStatusPanelListener(StatusPanelListener statusPanelListener) {
        this.statusPanelListener = statusPanelListener;
    }
}
