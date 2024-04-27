package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import clientHandler.ClientHandler;

@SuppressWarnings("resource")
public class Server {
    private static final int PORT = 8180;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado na porta " + PORT);

            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String logFileName = "logs/log-servidor-" + dateFormat.format(new Date()) + ".txt";
            File logFile = new File(logFileName);
            logFile.createNewFile();

            PrintStream fileStream = new PrintStream(new FileOutputStream(logFile));
            System.setOut(fileStream);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(
                        "Novo usuário conectado: " + clientSocket.toString() + ". Data da conexão:" + new Date().toString());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
