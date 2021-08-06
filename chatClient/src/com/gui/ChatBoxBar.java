package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatBoxBar extends JPanel {

    private Client client;
    private JTextField chatField;
    private JButton send;
    private ChatFieldListener chatFieldListener;

    public ChatBoxBar(Client client){
        this.client = client;

        chatField = new JTextField(30);
        send = new JButton("send");

        chatField.getPreferredSize().width = 20;
        setLayout(new FlowLayout(FlowLayout.TRAILING));

        add(new JScrollPane(chatField));
        add(send);

        // adding listener to send button
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String msg = chatField.getText() + "\n";
                chatField.setText("");
                if(msg.length() == 1){
                    //length == 1 indicates that there is only "\n"
                }else{
                    int count = 0;
                    for(char c : msg.toCharArray()){
                        if(c == ' ') count++;
                    }

                    if(count == msg.length() - 1) return;

                    if(chatFieldListener != null){
                        chatFieldListener.onChatMessage(msg);
                    }
                }
            }
        });
    }

    public void setChatFieldListener(ChatFieldListener chatFieldListener) {
        this.chatFieldListener = chatFieldListener;
    }
}
