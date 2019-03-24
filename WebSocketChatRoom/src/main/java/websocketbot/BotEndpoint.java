/**
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package websocketbot;

import websocketbot.encoders.UsersMessageEncoder;
import websocketbot.encoders.ChatMessageEncoder;
import websocketbot.encoders.InfoMessageEncoder;
import websocketbot.encoders.JoinMessageEncoder;
import websocketbot.messages.ChatMessage;
import websocketbot.messages.UsersMessage;
import websocketbot.messages.JoinMessage;
import websocketbot.messages.InfoMessage;
import websocketbot.decoders.MessageDecoder;
import websocketbot.messages.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.annotation.Resource;
//import javax.enterprise.concurrent.ManagedExecutorService;
//import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/* Websocket endpoint */
@ServerEndpoint(
        value = "/websocketbot",
        decoders = { MessageDecoder.class }, 
        encoders = { JoinMessageEncoder.class, ChatMessageEncoder.class, 
                     InfoMessageEncoder.class, UsersMessageEncoder.class }
        )
/* There is a BotEndpoint instance per connetion */
public class BotEndpoint {
    private static final Logger logger = Logger.getLogger("BotEndpoint");
    /* Bot functionality bean */
//    //@Inject
//    private BotStockBean botstockbean = new BotStockBean();
//    /* Executor service for asynchronous processing */
//    @Resource(name="tomcatThreadPool")
//    private ManagedExecutorService mes;

    static HashMap<String, Session> session_map=new HashMap<String, Session>();

    @OnOpen
    public void openConnection(Session session) {
        System.out.println("Connection opened: id:"+session.getId());
        session_map.put(session.getId(),session);
        logger.log(Level.INFO, "Connection opened.");
    }
    
    @OnMessage
    public void message(final Session session, Message msg) {
        System.out.println("Received: "+ msg.toString());
        logger.log(Level.INFO, "Received: {0}", msg.toString());
        
        if (msg instanceof JoinMessage) {
            /* Add the new user and notify everybody */
            JoinMessage jmsg = (JoinMessage) msg;
            session.getUserProperties().put("name", jmsg.getName());
            session.getUserProperties().put("active", true);
            logger.log(Level.INFO, "Received: {0}", jmsg.toString());
            sendAll(session, new InfoMessage(jmsg.getName() + " has joined the chat"));
        //    sendAll(session, new ChatMessage("Duke", jmsg.getName(), "Hi there!!"));
            sendAll(session, new UsersMessage(this.getUserList(session)));
            
        } else if (msg instanceof ChatMessage) {
            /* Forward the message to everybody */
            final ChatMessage cmsg = (ChatMessage) msg;
            logger.log(Level.INFO, "Received: {0}", cmsg.toString());
            sendAll(session, cmsg);
//            if (cmsg.getTarget().compareTo("Duke") == 0) {
//                /* The bot replies to the message */
//            	System.out.println("this is Duke'msg");
//                mes.submit(new Runnable() {
//                    public void run() {
//                        String resp = "Duke";//botstockbean.respond(cmsg.getMessage());
//                        sendAll(session, new ChatMessage("Duke", cmsg.getName(), resp));
//                    }
//                });
//            }
        }
    }
    
    @OnClose
    public void closedConnection(Session session) {
        /* Notify everybody */
        session.getUserProperties().put("active", false);
        session_map.remove(session.getId());
        if (session.getUserProperties().containsKey("name")) {
            String name = session.getUserProperties().get("name").toString();
            sendAll(session, new InfoMessage(name + " has left the chat"));
            sendAll(session, new UsersMessage(this.getUserList(session)));
        }
        System.out.println("Connection closed.");
        logger.log(Level.INFO, "Connection closed.");
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        logger.log(Level.INFO, "Connection error ({0})", t.toString());
    }
    
    /* Forward a message to all connected clients
     * The endpoint figures what encoder to use based on the message type */
    public synchronized void sendAll(Session session, Object msg) {
        try {
            for (String key :session_map.keySet()){
            for (Session s : session_map.get(key).getOpenSessions()) {
                if (s.isOpen()) {
                    if (msg instanceof ChatMessage) {
                        ChatMessageEncoder e = new ChatMessageEncoder();
                        s.getBasicRemote().sendText(e.encode((ChatMessage) msg));

                    } else if (msg instanceof InfoMessage) {
                        InfoMessageEncoder e = new InfoMessageEncoder();
                        s.getBasicRemote().sendText(e.encode((InfoMessage) msg));
                        System.out.println(e.encode((InfoMessage) msg));
                    } else if (msg instanceof JoinMessage) {
                        JoinMessageEncoder e = new JoinMessageEncoder();
                        s.getBasicRemote().sendText(e.encode((JoinMessage) msg));
                        System.out.println(((JoinMessage) msg).getName());
                    } else if (msg instanceof UsersMessage) {
                        UsersMessageEncoder e = new UsersMessageEncoder();
                        s.getBasicRemote().sendText(e.encode((UsersMessage) msg));
                    }
                    logger.log(Level.INFO, "Sent: {0}", msg.toString());
                    System.out.println(msg.toString());
                }
            }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
        }   
    }
    
    /* Returns the list of users from the properties of all open sessions */
    public List<String> getUserList(Session session) {
        List<String> users = new ArrayList<String>();
        //users.add("Duke");
        for (String key :session_map.keySet()) {
            for (Session s : session_map.get(key).getOpenSessions()) {
                if (s.isOpen() && s.getUserProperties().get("active").toString().equals("true")) {
                    users.add(s.getUserProperties().get("name").toString());
                }
            }
        }
        return users;
    }
}
