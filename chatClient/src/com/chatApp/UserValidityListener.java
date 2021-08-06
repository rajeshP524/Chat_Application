package com.chatApp;

public interface UserValidityListener {
    public void validUser(String username);
    public void invalidUser(String username);
}
