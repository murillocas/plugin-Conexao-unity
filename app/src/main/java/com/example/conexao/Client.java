package com.example.conexao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

    private String hostname;
    private int port;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() {
        try {
            Socket socket = new Socket(hostname, port);
            OutputStream outputStream = socket.getOutputStream();
            String message = "Hello from client!";
            outputStream.write(message.getBytes());
            outputStream.flush();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}