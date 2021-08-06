package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    private Client client;
    private ChatToolBar chatToolBar;
    private JTextArea textArea;
    private ChatBoxBar chatBoxBar;

    public ChatPanel(Client client){
        this.client = client;
        chatToolBar = new ChatToolBar(client);
        textArea = new JTextArea("this is chat area");
        chatBoxBar = new ChatBoxBar(client);

        chatToolBar.setHeadLabelText("messaging some one else");


        setLayout(new BorderLayout());
        add(chatToolBar, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(chatBoxBar, BorderLayout.SOUTH);

    }

    public ChatToolBar getChatToolBar() {
        return chatToolBar;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public ChatBoxBar getChatBoxBar() {
        return chatBoxBar;
    }
}
