package clientHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private String clientName;
    private List<ClientHandler> clients;
    private PrintWriter out;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            clientName = in.readLine();

            while (true) {
                String message = in.readLine();
                if (message.equals("/sair")) {
                    this.clientSocket.close();
                    System.out.println(clientName + " saiu do chat.");
                    break;
                } else if (message.equals("/users")) {
                    listUsers();
                    System.out.println(clientName + " solicitou a lista de usuários conectados.");
                } else if (message.equals("/voltar")) {
                    out.println("\n");
                } else if (message.equals("/send-file")) {
                    String recipientName = in.readLine();
                    receberArquivo(in, recipientName, clientName);
                } else {
                    sendMessageToRecipient(clientName, message);
                    System.out.println(clientName + " enviou uma mensagem.");
                }
            }

            clientSocket.close();
            System.out.println("Usuário desconectado: " + clientName);
            clients.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receberArquivo(BufferedReader in, String recipientName, String clientName) throws IOException {
        String fileName = in.readLine();
        String downloadsDir = System.getProperty("user.home") + File.separator + "Downloads";
        String filePath = downloadsDir + File.separator + fileName;

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath))) {
            String line = in.readLine();
            while (!(line = in.readLine()).equals("/file-end")) {
                fileWriter.write(line);
                fileWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(recipientName)) {
                String marginS = "\n-------------------------------------------------\n";
                String title = "Você recebeu um arquivo do usuário " + clientName;
                String marginI = "\n-------------------------------------------------";

                client.sendMessage(marginS + title + marginI);
                return;
            }
        }
    }

    private void listUsers() {
        StringBuilder userList = new StringBuilder();
        if (clients.size() > 1) {
            userList.append("Usuários conectados: ");
        } else {
            userList.append("Não há usuários conectados. ");
        }

        Integer iterator = 0;
        for (ClientHandler client : clients) {
            iterator++;
            if (!client.getClientName().equals(clientName)) {
                userList.append(client.getClientName()).append(", ");
            }
        }
        out.println(userList.toString());
    }

    private void sendMessageToRecipient(String sender, String message) {
        String[] parts = message.split(":", 2);
        String recipientName = parts[0];
        String content = parts[1];
        String marginS = "\n-------------------------------------------------\n";
        String title = "Mensagem recebida:\n";
        String marginI = "\n-------------------------------------------------";

        for (ClientHandler client : clients) {
            if (client.getClientName().equals(recipientName)) {
                client.sendMessage(marginS + title + sender + ": " + content + marginI);
                return;
            }
        }

        out.println("Usuário '" + recipientName + "' não encontrado.");
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
