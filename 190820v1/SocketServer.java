package socket_0820_v1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

				/*
				 * client -> server
				 */
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				String message = (String) ois.readObject();
				System.out.println("### Server Side: Message Received: " + message);

				/*
				 * server -> client
				 */
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject("Respond from Server: " + message);

				ois.close();
				oos.close();
				socket.close();

				if (message.equalsIgnoreCase("shutdown")) {
					System.out.println("### Server Side: got the CMD shutdown from client");
					break;
				}

			} catch (IOException e) {
				System.out.println("### Server Side: IOException :" + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			System.out.println("### Server Side: close socketserver");
			listerner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		(new SocketServer()).start();
	}
}
