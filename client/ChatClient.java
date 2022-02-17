package client;

import java.io.*;
import java.net.*;

public class ChatClient{
	private String hostname;
	private int port;
	private String userName;

	public ChatClient(String hostname , int port){
		this.hostname = hostname;
		this.port = port;
	}

	void chatClientRunning(){
		try{
			Socket socket = new Socket(hostname , port);
			System.out.println("connected to the chat server");

			new ReadThread(socket , this).start();
			new WriteThread(socket , this).start();
		}
		catch(UnknownHostException ex){
			System.out.println("Server not found " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(IOException ex){
			System.out.println("I/O error " + ex.getMessage());
			ex.printStackTrace();
		}
	}  
	public void setUserName(String username){
		this.userName = username;
	}

	public String getUserName(){
		return this.userName;
	}


	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
		String hostname = args[1];

		ChatClient client = new ChatClient(hostname , port);
		client.chatClientRunning();
	}


	
}


class ReadThread extends Thread{
	private BufferedReader br;
	private Socket socket;
	private ChatClient client;

	public ReadThread(Socket socket , ChatClient client){
		this.socket = socket;
		this.client = client;

		try{
			InputStream inputstream = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(inputstream));
		}
		catch(IOException ex){
			System.out.println("Error occured in getting InputStream " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run(){
		String response;
		while(true){
			try{
				response = br.readLine();
				System.out.println(response);
				if (client.getUserName() != null) 
				     System.out.println("\n [ " + client.getUserName() + " ] :" );
			}
			catch(IOException ex){
				System.out.println("Error in reading from the server "  + ex.getMessage());
				ex.printStackTrace();
				break;
			}

		}

	}
}


class WriteThread extends Thread{
	private PrintWriter writer;
	private ChatClient client;
	private Socket socket;

	public WriteThread(Socket socket , ChatClient client){
		this.socket = socket;
		this.client = client;

		try{
			OutputStream outputstream = socket.getOutputStream();
			writer = new PrintWriter(outputstream , true);
		}
		catch(IOException ex){
			System.out.println("Error occured in output stream " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter your name here");
			String userName = br.readLine();

			writer.println(userName);
			while(true){
				
			    System.out.println("[ " + userName + " ] : ");
			    String text = br.readLine();
			    writer.println(text);
			    if(text.equals("bye")==true)
				    break;
		    }
		}
		catch(IOException ex){
			System.out.println("Error occured in reading " + ex.getMessage());
			ex.printStackTrace();
		}
		try{
			socket.close();
		}
		catch(IOException ex){
			System.out.println("Error waiting to server " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}