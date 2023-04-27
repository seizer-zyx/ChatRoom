package com.socket.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private javax.swing.JPanel JPanel;
    private JTextField textField2;
    private JButton 连接Button;
    private JButton 发送Button;
    private JTextField textField1;
    private JTextField textField3;
    private JTextArea textArea1;
    private JTextField textField4;

    private Socket accept;

    public ChatClient() {
        连接Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (accept != null) {
                        accept.close();
                    }
                    String ip = textField1.getText();
                    int port = Integer.parseInt(textField2.getText());
                    String username = textField4.getText();

                    // 创建套接字进行连接
                    accept = new Socket(ip, port);

                    try {
                        // 发送username数据
                        DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
                        dataOutputStream.writeUTF(username);
                        dataOutputStream.flush();
                        // 开启俩个线程分别用于接收消息和发送消息
                        new ReceiveServer().start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        发送Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField3.getText();
//                System.out.println(message);
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    private class ReceiveServer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
                    String message = dataInputStream.readUTF();
                    System.out.println(message);
                    textArea1.append(message + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("ChatClient");
        frame.setContentPane(new ChatClient().JPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowsWidth = 400;
        int windowsHeight = 400;
        frame.setSize(windowsWidth, windowsHeight);
        Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
        Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
        int screenWidth = screenSize.width;                     //获取屏幕的宽
        int screenHeight = screenSize.height;                   //获取屏幕的高
        frame.setLocation((screenWidth - windowsWidth) / 2, (screenHeight - windowsHeight) / 2);//设置窗口居中显示
        frame.pack();
        frame.setVisible(true);
    }
}
