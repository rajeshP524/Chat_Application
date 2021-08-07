package com.gui;

import com.chatApp.Client;
import com.chatApp.UserValidityListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

public class ToolBar extends JPanel {
    private Client client;
    private JButton directMessage;
    private JButton chatRoom;
    private JButton exit;

    private DMessageListener dMessageListener;
    private ChatroomLabelSetter chatroomLabelSetter;

    public ToolBar(Client client){
        this.client = client;

        directMessage = new JButton("DirectMessage");
        chatRoom = new JButton("ChatRoom");
        exit = new JButton("Exit");

        setLayout(new FlowLayout());
        add(directMessage, FlowLayout.LEFT);
        add(chatRoom, FlowLayout.LEFT);
        add(exit, FlowLayout.RIGHT);

        // add listener for exit button
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int option = JOptionPane.showConfirmDialog(ToolBar.this, "Are you sure to exit? ", "exit", JOptionPane.OK_CANCEL_OPTION);
                if(option == JOptionPane.OK_OPTION){
                    try {
                        client.handleExit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }

            }
        });

        // add Listener for directMessage
        directMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = JOptionPane.showInputDialog(ToolBar.this, "Enter username:" );
                if(username != null){
                    if(username.length() == 0){
                        JOptionPane.showMessageDialog(ToolBar.this, "No such user exits", "message", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        client.isValidUser(username);
                    }
                }
            }
        });

        // add Listener for chatRoom
        chatRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String cmd = JOptionPane.showInputDialog(ToolBar.this, "Enter a command: join, leave, msg" );
                if(cmd.equalsIgnoreCase("join")){
                    cmd = "join chatroom\n";
                    if(client.isMemberOfChatroom()){
                        JOptionPane.showMessageDialog(ToolBar.this, "You are already a member of chatroom", "message", JOptionPane.ERROR_MESSAGE);
                    }else{

                        // add client to chatroom and upadate information in client
                        client.joinChatroom(cmd);
                        client.setMemberOfChatroom(true);
                        JOptionPane.showMessageDialog(ToolBar.this, "You are added to chatroom", "message", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if(cmd.equalsIgnoreCase("leave")){
                    cmd = "leave chatroom\n";

                    if(client.isMemberOfChatroom()){
                        // if you are a member of chatroom, you can exit and update information in client class
                        client.leaveChatroom(cmd);
                        client.setMemberOfChatroom(false);
                        JOptionPane.showMessageDialog(ToolBar.this, "You are no more a member of chatroom", "message", JOptionPane.INFORMATION_MESSAGE);

                    }else{
                        JOptionPane.showMessageDialog(ToolBar.this, "You are already not a member of chatroom", "message", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if(cmd.equalsIgnoreCase("msg")){
                    if(client.isMemberOfChatroom()){
                        // send messages at this room
                        //set label to #chatroom in chatToolBar
                        if(chatroomLabelSetter != null){
                            chatroomLabelSetter.setLabel();
                        }

                    }else{
                        // not allowed to send messages at this room
                        JOptionPane.showMessageDialog(ToolBar.this, "You can't send messages at this room! execute JOIN command", "message", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(ToolBar.this, "unknown command", "error command", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // add toolBar as a listener for user validness
        client.setUserValidityListener(new UserValidityListener() {
            @Override
            public void validUser(String username) {
                if(dMessageListener != null){
                    dMessageListener.onValidUser(username);
                }
            }

            @Override
            public void invalidUser(String username) {
                JOptionPane.showMessageDialog(ToolBar.this, "No such user exits", "message", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setdMessageListener(DMessageListener dMessageListener) {
        this.dMessageListener = dMessageListener;
    }

    public void setChatroomLabelSetter(ChatroomLabelSetter chatroomLabelSetter) {
        this.chatroomLabelSetter = chatroomLabelSetter;
    }
}
