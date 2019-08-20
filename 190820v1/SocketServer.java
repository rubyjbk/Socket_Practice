package socket_0820_v1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends java.lang.Thread{
	
	private static ServerSocket server;
	
	private static int port=9876;
	
	public SocketServer() {
		try{
			server=new ServerSocket(port);
		}
		catch(IOException e) {
			System.out.println("IOException :" + e.toString());
		}
	}
	
	public void run() {
		while(true){
			System.out.println("Waiting for the client request");
			
			try {
				Socket socket=server.accept();
				
				/*
				 *  client -> server
				 */
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				String message = (String) ois.readObject();
	            System.out.println("Message Received: " + message);
	            
	            /*
				 *  server -> client
				 */
	            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	            oos.writeObject("Hi Client "+message);
	            
	            ois.close();
	            oos.close();
	            socket.close();
	            
	            if(message.equalsIgnoreCase("shutdown")) {
	            	System.out.println("Go to shutdown");
	            	break;
	            }
	            
			}catch(IOException e) {
				System.out.println("IOException :" + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Shutting down Socket server!!");
		
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		(new SocketServer()).start();
	}
}
