package multithread_socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
 
public class MultithreadSocket extends Thread {
 
	private ServerSocket listerner;
 
	private Socket socket;
 
	private int connection_cnt = 0;
 
	public MultithreadSocket(ServerSocket listerner, Socket socket, int connection_cnt) {
		this.listerner = listerner;
		this.socket = socket;
		this.connection_cnt = connection_cnt;
 
	}
 
	public void run() {
		boolean exit=false;
		while (!exit) {
			System.out.println(
					"### Server Side: running and waiting for request #" + Integer.toString(this.connection_cnt));
 
			try {
				ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
				//DataInputStream dis=new DataInputStream(this.socket.getInputStream());
				BufferedInputStream inFromClient = new BufferedInputStream(this.socket.getInputStream());
				int idx = 0, content_size = 0;
				StringBuilder sb01 = new StringBuilder();
				StringBuilder sb02 = new StringBuilder();
				String str01 = null;
				String str02 = null;
				boolean is_cmd = false, is_uac = false;
 
				while (true) {
					
 
					int b = inFromClient.read();
					System.out.println("Char: " + (char) b);
 
					if (idx <= 2) {
						sb01.append((char) b);
						if (idx == 2) {
							str01 = sb01.toString();
							if (str01.equals("CMD")) {
								System.out.println("### Server Side: Message Received: " + str01);
								is_cmd = true;
 
							} else if (str01.equals("UAC")) {
								System.out.println("### Server Side: Message Received: " + str01);
								is_uac = true;
							}
 
						}
 
					}
 
					else if (idx <= 6) {
						System.out.println("f: " + Integer.toString(b));
						if (idx == 3) {
							content_size += b * 1000;
						} else if (idx == 4) {
							content_size += b * 100;
						} else if (idx == 5) {
							content_size += b * 10;
						} else if (idx == 6) {
							content_size += b;
							System.out.println("### Server Side: content_size " + Integer.toString(content_size));
						}
					} else if (idx <= 6 + content_size) {
						sb02.append((char) b);
						if (is_cmd == true) {
							if (idx == 14) {
								str02 = sb02.toString();
								if (str02.equals("SHUTDOWN")) {
									System.out.println("### Server Side: Message Received: " + str02);
 
									oos.writeObject(Integer.toString(this.connection_cnt));
									oos.flush();
									oos.close();
 
									inFromClient.close();
									this.socket.close();
									this.listerner.close();
									System.out.println("### Server Side: close socketserver");
									exit=true;
									break;
 
									// function_shutdown();
								}
							}
						} else if (is_uac == true) {
							if (idx == 6 + content_size) {
								str02 = sb02.toString();
								int cnt_unique_username=0;
								Set<String> set=new HashSet<String>();
								String cvsSplitBy = ",";
								Scanner scanner = new Scanner(str02);
								System.out.println(str02);
								while (scanner.hasNextLine()) {
									String line = scanner.nextLine();
									String[] field = line.split(cvsSplitBy);
									System.out.println("1-ffff----");
									System.out.println(field[1]);
									System.out.println("2-----");
									String preferredUsername=field[2];
									System.out.println(preferredUsername);
									if(set.contains(preferredUsername)==false) {
										set.add(preferredUsername);
										cnt_unique_username++;
									}
									
									//System.out.println("### Server Side: [displayName= " + field[1]+ ", preferredUsername=" + field[2] + "]");
								}
								scanner.close();
								
								oos.writeObject(Integer.toString(cnt_unique_username-1));
								oos.flush();
								oos.close();
								inFromClient.close();
								this.socket.close();
								exit=true;
								break;
 
							}
 
						}
 
					} else {
 
					}
 
					idx++;
					
 
				}
 
			} catch (IOException e) {
				System.out.println("### Server Side: IOException :" + e.toString());
			}
			/*
			 * catch (ClassNotFoundException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
		}
 
	}
 
	public static void main(String args[]) throws IOException {
 
		int connection_cnt = 0;
		int port = 9876;
 
		if (args.length == 1) {
			port = Integer.valueOf(args[0]);
		}
 
		ServerSocket ss = new ServerSocket(port);
 
		// running infinite loop for getting
		// client request
		while (true) {
			Socket s = null;
 
			try {
				// socket object to receive incoming client requests
				s = ss.accept();
 
				if (connection_cnt < 12) {
					System.out.println("A new client is connected : " + s);
 
					System.out.println("Assigning new thread for this client");
 
					// create a new thread object
					connection_cnt++;
					Thread t = new MultithreadSocket(ss, s, connection_cnt);
 
					// Invoking the start() method
					t.start();
				} else {
					System.out.println("Error: maximum number of the threads the server can support is 12");
				}
 
			} catch (Exception e) {
				s.close();
				e.printStackTrace();
			}
		}
	}
}
