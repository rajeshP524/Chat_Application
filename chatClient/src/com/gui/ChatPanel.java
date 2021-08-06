package com.gui;

import com.chatApp.Client;
import com.chatApp.MessageListener;

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
        textArea = new JTextArea();
        chatBoxBar = new ChatBoxBar(client);

        chatToolBar.setHeadLabelText("Welcome!");


        setLayout(new BorderLayout());
        add(chatToolBar, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(chatBoxBar, BorderLayout.SOUTH);

        //chatPanel as a listener
        chatBoxBar.setChatFieldListener(new ChatFieldListener() {
            @Override
            public void onChatMessage(String message) {
                String label = chatToolBar.getHeadLabelText();
                String tokens[] = label.split(" ");
                String cmd = "msg " + tokens[2] + " " + message;
                //send msg command to server to send message to this particular user
                client.message(cmd);
                textArea.append("you# " + message);
            }
        });

        //chatPanel as a listener for receiving messages
        client.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(String sender, String msgBody) {
                String msg = sender + "# " + msgBody + "\n";

                String tokens[] = chatToolBar.getHeadLabelText().split(" ");
                // check if present opened user is a valid one, to append the text
                if(sender.equalsIgnoreCase(tokens[2])){
                    textArea.append(msg);
                }
            }
        });


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

    //chatPanel as listener for DMessage Button(for valid users) through mainFrame
    public void listenUserNameFromMainFrame(String username, MainFrame mainFrame){
        String label = "Message : " + username;
        chatToolBar.setHeadLabelText(label);
        mainFrame.setChatArea(username);
    }
}
