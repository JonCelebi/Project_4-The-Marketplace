import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Project - Project 5  -  Server
 * This program is used to store all the files and maintain a concurrent program for multiple users to access the
 * market at the same time, while reflecting all updates.
 *
 * @author Shenggang Liu, Jonathan Cerda, Emily Barone, Tamanna Sahoo  lab sec L14
 * @version April 28, 2023
 */

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242); //port number must be 4242
        serverSocket.setReuseAddress(true);


    while (true) {
        Socket userSocket = null;
        try {
            userSocket= serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(userSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(userSocket.getInputStream());
            ClientHandler newClient = new ClientHandler(userSocket, oos, ois);
            new Thread(newClient).start();
        }catch (Exception e) {
            userSocket.close();
            e.printStackTrace();
        }
    }

    }


}