package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{

	private int port;
	private Set<String> userNames = new HashSet<>();
	private Set<UserThread> userThreads = new HashSet<>();


	public ChatServer(int port){
		this.port=port;
	}


	public void startRunning(){
		try(ServerSocket serversocket = new ServerSocket(port)){
			System.out.println("ChatServer is running at port no. " + port);
				
			while(true){

				Socket socket = serversocket.accept();
				UserThread newUser = new UserThread(socket , this);
				userThreads.add(newUser);
				newUser.start();
		    }
		}
		catch(IOException ex){
			System.out.println("Error has occured in server " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		int port1 = Integer.parseInt(args[0]);
		ChatServer server = new ChatServer(port1);

		server.startRunning();
	}


	void broadcast(String message , UserThread withoutUser){
		for(UserThread user : userThreads)
		{
			if(user!=withoutUser){
				user.sendMessage(message);
			}
		}
	}

	void addUser(String userName){
		userNames.add(userName);
	}

	void removeUser(String userName , UserThread remove){
		userNames.remove(userName);
		userThreads.remove(remove);

		System.out.println("The user " + userName + " quitted");
	}

	Set<String> getUserNames(){
		return this.userNames;
	}

	boolean hasUsers(){
		return !this.userNames.isEmpty();
	}
	
}



class UserThread extends Thread{
	private Socket socket;
	private ChatServer server1;
	private PrintWriter writer;

	public UserThread(Socket socket , ChatServer server2){
		this.socket=socket;
		this.server1=server2;
	}
	public void sendMessage(String message){
		writer.println(message);
	}
	public void run(){
		
		try{
			    InputStream inputstream = socket.getInputStream();
			    BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));

			    OutputStream outputstream = socket.getOutputStream();
			    writer = new PrintWriter(outputstream , true);


			    printUsers();
			    
			    String userName = br.readLine();
			    server1.addUser(userName);
			    String serverMessage = "A new user is connnected " + userName;
			    server1.broadcast(serverMessage , this);

			    String userMessage = null; 
			    do{
				    userMessage = br.readLine();
				    serverMessage = "[ " + userName + " ] : " + userMessage;
				    server1.broadcast(serverMessage , this);
			    }while(!userMessage.equals("bye"));

			    server1.removeUser(userName , this);
			    socket.close();
			    serverMessage = userName + " has quitted ";
			    server1.broadcast(serverMessage , this);
		}
		catch(Exception ex){
				System.out.println("Error in userThread " + ex.getMessage());
			 	ex.printStackTrace();
		}
	}

	void printUsers() {
        if (server1.hasUsers()) {
            writer.println("Connected users: " + server1.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }
}