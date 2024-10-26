package com.panda.controller;

//import panda.financeapp.model.User;

public class SessionManager {
    public static final ThreadLocal<SessionManager> currentSession = new ThreadLocal<SessionManager>();
    private int userId;

    private SessionManager(int userId) {
        this.userId = userId;
    }

    public static SessionManager getCurrentSession() {
        return currentSession.get();
    }
    public static void startSession(int userId) {
        SessionManager session = new SessionManager(userId);
        currentSession.set(session);
    }
    public static void stopSession(){
        currentSession.remove();
    }
    public int getUserId() {
        return userId;
    }
}