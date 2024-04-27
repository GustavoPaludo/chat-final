package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 8180;
	private static String name = null;

	private static BufferedReader in;
	private static PrintWriter out;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		try {
			Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Digite seu nome: ");
			name = userInput.readLine();
			out.println(name);

			System.out.println("Bem vindo ao chat, " + name + "!");

			new Thread(() -> {
				try {
					String message;
					while ((message = in.readLine()) != null) {
						System.out.println(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();

			showMenu();

		    System.exit(0);
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void showMenu() throws InterruptedException {
		try {
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

			String userMessage;
			while (true) {
				Thread.sleep(1000);
				System.out.println("-------------------------------------------------");
				System.out.println("Logado como: " + name);
				System.out.println("\nDigite uma opção:");
				System.out.println("/users");
				System.out.println("/send-message");
				System.out.println("/send-file");
				System.out.println("/sair");
				System.out.println("-------------------------------------------------");

				userMessage = userInput.readLine();

				switch (userMessage) {
				case "/users":
					out.println("/users");
					break;
				case "/send-message":
					sendMessage();
					break;
				case "/send-file":
					sendFile();
					break;
				case "/sair":
					out.println("/sair");
					return;
				default:
					System.out.println("Opção inválida!");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sendMessage() throws IOException, InterruptedException {
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("(Digite '/return' para voltar ao menu): ");
		System.out.print("Digite o nome do usuário com o qual deseja se conectar: ");
		String recipientName = userInput.readLine();
		if (recipientName.equals("/return")) {
			out.println("/return");
			return;
		}
		System.out.print("\nMensagem: ");
		String message = userInput.readLine();
		if (message.equals("/return")) {
			out.println("/return");
			return;
		}
		out.println("/send-message");
		out.println(recipientName + ":" + message);
	}

	private static void sendFile() throws IOException {
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("(Digite '/return' para voltar ao menu): ");
		System.out.print("Digite o nome do usuário para quem deseja enviar o arquivo: ");
		String recipientName = userInput.readLine();
		if (recipientName.equals("/return")) {
			out.println("/return");
			return;
		}
		System.out.print("\nDigite o caminho completo do arquivo que deseja enviar: ");
		String filePath = userInput.readLine();
		if (filePath.equals("/return")) {
			out.println("/return");
			return;
		}
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			System.out.println("Arquivo inválido.");
			return;
		}

		String fileName = file.getName();
		out.println("/send-file");
		out.println(recipientName);
		out.println(fileName);
		out.println(file.length());

		try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = fileReader.readLine()) != null) {
				out.println(line);
			}
			out.println("/file-end");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
