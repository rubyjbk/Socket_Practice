package socket_0820_v1;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SocketServer extends java.lang.Thread {

	private static ServerSocket listerner;

	private static int port = 9876;

	public SocketServer() {
		try {
			listerner = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("IOException :" + e.toString());
		}
	}

	public void run() {
		while (true) {
			System.out.println("### Server Side: running and waiting for request");

			try {
				Socket socket = listerner.accept();

				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				String message = (String) ois.readObject();
				System.out.println("### Server Side: Message Received: " + message);

				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject("Respond from Server: " + message);

				if (message.equalsIgnoreCase("shutdown")) {
					System.out.println("### Server Side: got the CMD shutdown from client");
					function_shutdown();
					break;
				} else if (message.equalsIgnoreCase("parseCSV")) {
					System.out.println("### Server Side: got the CMD parseCSV from client");
					function_parseCSV(socket);
					break;
				}

				ois.close();
				oos.close();
				socket.close();

			} catch (IOException e) {
				System.out.println("### Server Side: IOException :" + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void function_shutdown() {
		try {
			System.out.println("### Server Side: close socketserver");
			listerner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void function_parseCSV(Socket socket) {

		BufferedInputStream in;
		try {
			in = new BufferedInputStream(socket.getInputStream());
			byte[] b = new byte[1024];
			String data = "", cvsSplitBy = ",";
			int length;
			while ((length = in.read(b)) > 0) {
				data += new String(b, 0, length);
			}

			Scanner scanner = new Scanner(data);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] country = line.split(cvsSplitBy);
				System.out.println("### Server Side: Country [code= " + country[4] + " , name=" + country[5] + "]");
			}
			scanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		(new SocketServer()).start();
	}
}
