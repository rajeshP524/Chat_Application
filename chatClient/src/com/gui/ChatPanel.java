package com.gui;

import com.chatApp.ChatroomMessageListener;
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

        //chatPanel as a listener for chatField
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

                if(tokens.length != 3) return;

                // check if present opened user is a valid one, to append the text
                if(sender.equalsIgnoreCase(tokens[2])){
                    textArea.append(msg);
                }
            }
        });

        // chatPanel as a listener for receiving chatroom messages
        client.setChatroomMessageListener(new ChatroomMessageListener() {
            @Override
            public void onChatroomMessage(String sender, String msgBody) {
                String msg = sender + "# " + msgBody + "\n";

                String[] tokens = chatToolBar.getHeadLabelText().split(" ");

                if(tokens.length != 3) return;

                //check if present open one is a chatroom to append the text
                if("#chatroom".equalsIgnoreCase(tokens[2])){
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

    //chatPanel as a listener for chatroom laber setting(for a valid member of chatroom) through mainFrame
    public void listenChatroomLabelSetFromMainFrame(MainFrame mainFrame){
        String label = "Message : " + "#chatroom";
        chatToolBar.setHeadLabelText(label);
        mainFrame.setChatArea("#chatroom");
    }
}
