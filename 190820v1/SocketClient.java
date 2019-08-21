package socket_0820_v1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {

	private static String address = "127.0.0.1";
	private static int port = 9876;

	public SocketClient(String cmd, String param) {

		if (cmd.equals("test")) {
			function_test(param);
		} else if (cmd.equals("shutdown")) {
			function_shutdown();
		} else if (cmd.equals("parseCSV")) {
			function_parseCSV(param);
		}

	}

	public void function_test(String param) {
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		int routine = Integer.parseInt(param);

		for (int i = 0; i < routine; i++) {
			try {
				socket = new Socket(address, port);
				// write to socket using ObjectOutputStream
				oos = new ObjectOutputStream(socket.getOutputStream());
				System.out.println("*** Client Side: sending request to server");
				oos.writeObject("ping : " + i);

				// read the server response message
				ois = new ObjectInputStream(socket.getInputStream());
				String message = (String) ois.readObject();
				System.out.println("*** Client Side: got the respond message: " + message);
				// close resources
				ois.close();
				oos.close();
				socket.close();

				// Thread.sleep(100);

			} catch (IOException e) {
				System.out.println("IOException :" + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void function_shutdown() {
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			socket = new Socket(address, port);
			// write to socket using ObjectOutputStream
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("*** Client Side: sending request to server");
			oos.writeObject("shutdown");

			// read the server response message
			ois = new ObjectInputStream(socket.getInputStream());
			String message = (String) ois.readObject();
			System.out.println("*** Client Side: got the respond message: " + message);

			// close resources
			ois.close();
			oos.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("*** Client Side: Exception : " + e.toString());
		}

	}

	public void function_parseCSV(String param) {
		String fileName = param;
		System.out.println("*** Client Side: fileName : " + fileName);

		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;

		try {
			socket = new Socket(address, port);

			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("*** Client Side: sending request to server");
			oos.writeObject("parseCSV");
			oos.flush();

			File myFile = new File(fileName);
			byte[] mybytearray = new byte[(int) myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
			os = socket.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();

			ois = new ObjectInputStream(socket.getInputStream());
			String message = (String) ois.readObject();
			System.out.println("*** Client Side: got the respond message: " + message);

			oos.close();
			ois.close();
			socket.close();

		} catch (Exception e) {
			System.out.println("IOException :" + e.toString());
		}

	}

	public static void main(String args[]) {
		if (args.length == 0) {
			new SocketClient("test", "5");
		} else if (args.length == 1) {
			new SocketClient(args[0], "");
		} else if (args.length == 2)
			new SocketClient(args[0], args[1]);
		else {
			System.out.println("");
		}
	}

}
