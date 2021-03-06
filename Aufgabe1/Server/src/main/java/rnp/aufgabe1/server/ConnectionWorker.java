/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server;

import java.io.*;
import java.net.Socket;

/**
 * Created by flbaue on 13.10.14.
 */
public class ConnectionWorker implements Runnable {

    private static int instanceCounter = 0;

    public final int instance;
    private final Socket clientSocket;
    private final CommandProcessor processor = new CommandProcessor(this);
    private final Server server;
    private boolean isStopped = false;
    private boolean hasReceived = false;

    public ConnectionWorker(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.instance = ++instanceCounter;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream(), "UTF-8"));

            while (!isStopped) {
                String input = readIncomingText(in, 255);
                String output = processInput(input);

                out.write(output);
                out.flush();
                
                hasReceived = true;
            }

            in.close();
            out.close();

        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            closeSocket();
        }

        server.removeConnection(this);
    }
    
    

    public boolean isHasReceived() {
		return hasReceived;
	}

	public void setHasReceivedFalse() {
		this.hasReceived = false;
	}

	private String processInput(String input) {
        String output = processor.process(input);
        if (output.startsWith("BYE") || output.startsWith("OK_BYE")) {
            isStopped = true;
        }
        return output;
    }

    public boolean stopServer(String secretToken) {
        return server.stop(secretToken);
    }

    private String readIncomingText(BufferedReader in, int length)
            throws IOException {
        char[] buffer = new char[length];
        int c = 0;
        int count = 0;
        while (c != 0x0A && count < 255) {
            c = in.read();
            if (c != -1) {
                buffer[count] = (char) c;
                count++;
            }
        }

        String input = String.valueOf(buffer);

        int end = input.indexOf('\u0000');
        if (end != -1) {
            input = input.substring(0, end);
        }
        return input;

    }

    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            // throw new RuntimeException("Error while closing the socket", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ConnectionWorker that = (ConnectionWorker) o;

        if (instance != that.instance)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return instance;
    }

	public void closeConnection() {
		closeSocket();
		server.removeConnection(this);
	}
}
