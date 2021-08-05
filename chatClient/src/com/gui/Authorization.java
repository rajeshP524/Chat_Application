package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class Authorization extends JFrame {
    private Client client;
    private JTextField textField;
    private JPasswordField passwordField;
    private JButton login;
    private JButton register;

    public Authorization(Client client){
        super("Authorization");
        this.client = client;

        // setting properties for authorization frame
        setVisible(true);
        setSize(450, 270);
        setMinimumSize(new Dimension(400,270));


        setLayout(new BorderLayout());

        //components
        textField = new JTextField(15);
        passwordField = new JPasswordField(15);
        login = new JButton("login");
        register = new JButton("register");



        // setting gridBagLayout
        layoutComponents();


        // Adding action listeners to both the buttons login and register
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = textField.getText();
                String password = passwordField.getText();

                boolean isSuccessfulLogin = false;
                try {
                    isSuccessfulLogin = client.login(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(isSuccessfulLogin){
                    JOptionPane.showMessageDialog(Authorization.this, "login successful", "message", JOptionPane.INFORMATION_MESSAGE);
                    Authorization.this.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(Authorization.this, "error login", "message", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = textField.getText();
                String password = passwordField.getText();

                String response = "";

                try {
                    response = client.register(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(response.equalsIgnoreCase("registration successful")){
                    JOptionPane.showMessageDialog(Authorization.this, "registration successful", "message", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(Authorization.this, response, "message", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;

        //first row
        gc.weightx = 1;
        gc.weighty = 1;

        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0,0,0,5);
        add(new JLabel("username : "), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0,0,0,0);
        add(textField,gc);

        //next row
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridy++;

        gc.gridx = 0;
        gc.anchor = GridBagConstraints.FIRST_LINE_END;
        gc.insets = new Insets(0,0,0,5);
        add(new JLabel("password : "), gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(0,0,0,0);
        add(passwordField, gc);

        //next row
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridy++;

        gc.gridx = 0;
        gc.anchor = GridBagConstraints.FIRST_LINE_END;
        gc.insets = new Insets(0,20,0,5);
        add(login, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(0,0,0,0);
        add(register, gc);


    }

}
