package com.socket.chatroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private static HashMap<String, User> users = new HashMap<>();

    private static class User extends Thread {

        private String username;
        private Socket accept;


        public User(String username, Socket accept) {
            this.username = username;
            this.accept = accept;
        }


        @Override
        public void run() {
            try {
                while (true) {
                    DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
                    String message = dataInputStream.readUTF();
                    System.out.println(username + ": " + message);
                    SendAll(username + ": " + message);
                }
            } catch (IOException e) {
                try {
                    accept.close();
                    users.remove(username);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private static void SendAll(String mseeage) {
        for (User user : users.values()) {
            boolean flag = SendMessage(user.accept, mseeage);
            if (flag != true) {
                users.remove(user.username);
            }
        }
    }

    private static boolean SendMessage(Socket accept, String message) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            return true;
        } catch (IOException e) {
            try {
                accept.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serversocket = new ServerSocket(1314);
            while (true) {
                Socket accept = serversocket.accept();
                DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
                String username = dataInputStream.readUTF();
                if (users.keySet().contains(username)) {
                    System.out.println(username + "聊天室中已经存在同名用户！");
                    SendMessage(accept, "聊天室中已经存在同名用户！");
                    accept.close();
                } else {
                    System.out.println(username + "加入聊天室");

                    User user = new User(username, accept);
                    users.put(username, user);
                    user.start();

                    SendAll(username + "加入聊天室");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
