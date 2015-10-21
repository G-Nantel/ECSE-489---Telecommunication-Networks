import java.io.*;
import java.nio.ByteBuffer;
import java.net.*;

public final class Client {
	
	private static boolean loggedIn = false, exit = false;
	

	public static Message ServerResponded = new Message(MessageType.ECHO, 0, "");
	
	public static void main(String[] args) throws Exception {
		String URL = "ecse-489.ece.mcgill.ca";
		int port = 5007;
		
		//set up input stream from user
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

		
		//Set up the client socket
		InetAddress serverAddress = InetAddress.getByName(URL);
		Socket clientSocket = new Socket(serverAddress.getHostAddress(), port);

		
		//create output stream
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		//create input stream from server
		BufferedInputStream inFromServer = new BufferedInputStream(clientSocket.getInputStream());
		
		//  welcome message
		System.out.println("\nHi, you are now connected to our Chat platform");
		
		//Setting up the receiving thread
		ThreadMReceived receiveThread = new ThreadMReceived(inFromServer);
		receiveThread.start();
		
		//Set up the querying thread
		new QueryThread(outToServer).start();
		
		
		while(!exit){
			while(!loggedIn && !exit){
				
				//Prompt the user to know if they would like to log in or sign up to create a new account
				System.out.println("If you would like to sign-in type:'In'");
				System.out.println("If you would like to sign-up type:'Up'");
				System.out.println("If you would like to exit type:'Exit'");
				
				String sign = inFromUser.readLine();
				
				//Verify if the user wants to sign in or sign up
				if (sign.equalsIgnoreCase("in")){
					//the user thinks he already has an account
					String username = "", password = "";
					boolean usernameValid = false, passwordValid= false;
					
					while (!usernameValid || !passwordValid) {
						System.out.println("Please enter your username:");
						username = inFromUser.readLine();
						//check whether the entered username is valid
						if (username.isEmpty() || username.contains(",") || username.contains(" ") ) {
							System.out.println("Please enter a valid username");
						}else {
							usernameValid = true;
							System.out.println("Please enter your password:");
							password = inFromUser.readLine();
							if (password.isEmpty() || password.contains(",") ) {
								System.out.println("Please enter a valid password");
							}else {
								passwordValid = true;
								System.out.println("Processing request..");
							}
							
						}
					}
					sendMessage(new Message(MessageType.LOGIN, username + "," + password), outToServer, true);
					if(ServerResponded.getSubMessageType() == 0)
					loggedIn = true;
				} else if(sign.equalsIgnoreCase("up")){
					
					String username = "", password = "";
					boolean usernameValid = false, passwordValid= false;
					
					while (!usernameValid || !passwordValid) {
						System.out.println("Please enter new username:");
						username = inFromUser.readLine();
						//check whether the entered username is valid
						if (username.isEmpty() || username.contains(",")  || username.contains(" ") ) {
							System.out.println("Please enter a valid username with no commas or spaces");
						}else {
							usernameValid = true;
							System.out.println("Please enter new password:");
							password = inFromUser.readLine();
							if (password.isEmpty() || password.contains(",") ) {
								System.out.println("Please enter a valid password");
							}else {
								passwordValid = true;
								System.out.println("Processing request..");
							}
							
						}
					}
					sendMessage(new Message(MessageType.CREATE_USER, username +","+ password),outToServer,true);
					if(ServerResponded.getSubMessageType() == 0)	
					sendMessage(new Message(MessageType.LOGIN, username +","+ password),outToServer,true);
					if(ServerResponded.getSubMessageType() == 0){
						loggedIn = true;
						sendMessage(new Message(MessageType.CREATE_STORE, ""),outToServer,true);
					}
				} else if(sign.equalsIgnoreCase("exit")){
					
					
					exit = true;
					sendMessage(new Message(MessageType.EXIT, ""), outToServer,false);
					
					
				}else{
					System.out.println("\nError: Please type 'In' or 'Up'\n");
				}
				
			}
			
			while(loggedIn){
				System.out.println("Type a command. To see the list of all commands, type 'All'");
				
				String sign = inFromUser.readLine();
				
				if (sign.equalsIgnoreCase("All")){
					System.out.println("\nLogoff \t\t\t- Logs you off\nDelete \t\t\t- Permanently deletes your account\n@<user> <message> \t- Send <message> to <user>\nExit \t\t\t- Logoff and close connection\n\nPlease type a command");
					sign = inFromUser.readLine();
				} 
				
				
				if(sign.equalsIgnoreCase("logoff")){
					sendMessage(new Message(MessageType.LOGOFF, ""), outToServer,true);	
					loggedIn = false;
					try{
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if(sign.equalsIgnoreCase("delete")){
					sendMessage(new Message(MessageType.DELETE_USER, ""), outToServer,true);
					loggedIn = false;
				}else if(sign.length() > 0 && sign.charAt(0) == '@' && sign.indexOf(" ") != -1){
					sign = sign.replaceFirst("@", "");
					String[] s=sign.split(" ", 2);
					String user = s[0];
					String message =  s[1];
					 if(message.length() == 0){
						 System.out.println("\nError: You did not type a message to "+user+". Please type one now");
						message = inFromUser.readLine();
					 }
					sendMessage(new Message(MessageType.SEND_MESSAGE, user+","+message), outToServer,true);
					
				} else if(sign.equalsIgnoreCase("exit")){
					
					exit = true;
					loggedIn = false;
					sendMessage(new Message(MessageType.EXIT, ""), outToServer,false);
					
				}else{
					System.out.println("\nError: Please type valid command\n");
				}
			}
			
		}
		clientSocket.close(); 
	}	

	///CHANGE PRIVATE FROM PUBLIC FOR TESTING
	public static void sendMessage(Message message, DataOutputStream outToServer, boolean wait) throws IOException{
		//Convert the data in bytes
		byte[] dataBytes = message.getData().getBytes();

		//Get the length of the dataBytes
		int dataSize = dataBytes.length;
		int dataSize = dataBytes.length;

		//Get all of the necessary ints in bytes
		byte[] messageTypeBytes = ByteBuffer.allocate(4).putInt(message.getMessageType().getMessageTypeInt()).array();
		byte[] subMessageTypeBytes = ByteBuffer.allocate(4).putInt(message.getSubMessageType()).array();
		byte[] dataSizeBytes = ByteBuffer.allocate(4).putInt(dataSize).array();
		

		//Put it all into one array of bytes
		byte[] messageBytes = new byte[12 + dataSize];
		System.arraycopy(messageTypeBytes, 0, messageBytes, 0, 4);
		System.arraycopy(subMessageTypeBytes, 0, messageBytes, 4, 4);
		System.arraycopy(dataSizeBytes, 0, messageBytes, 8, 4);
		System.arraycopy(dataBytes, 0, messageBytes, 12, dataSize);

		//Send the message
		outToServer.write(messageBytes,0,12 + dataSize); 
		
		
		
		if(wait){
			while(ServerResponded.getMessageType().getMessageTypeInt() != message.getMessageType().getMessageTypeInt()){
				try{
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

	public static Message receiveMessage(BufferedInputStream inFromServer) throws IOException{
		//read response and save as message
		byte[] messageBytes = new byte[4];

		for(int i = 0; i < 4; i++){
			messageBytes[i] = (byte) inFromServer.read();

		}
		int int1 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//System.out.println(int1);
		
		for(int i = 0; i < 4; i++){
			messageBytes[i] = (byte) inFromServer.read();
		}
		int int2 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//System.out.println(int2);
		
		for(int i = 0; i < 4; i++){
			messageBytes[i] = (byte) inFromServer.read();
		}
		int int3= messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//System.out.println(int3);
		
		byte[] messageString = new byte[int3];
		for(int i = 0; i < int3; i++){
			messageString[i] = (byte) inFromServer.read();

		}
		String string = new String(messageString);
		//System.out.println(string);

		Message message = new Message(MessageType.getMessageType(int1), int2, string);
		
		

		return message;
	}


	private static class QueryThread extends Thread{
		
		
		private DataOutputStream outToServer;
		public QueryThread(DataOutputStream outToServer){
			this.outToServer = outToServer;
		}
		
		
		public void run(){
			
			while(true){
				//Wait one second
				try{
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(ServerResponded.getMessageType().getMessageTypeInt() == MessageType.LOGOFF.getMessageTypeInt())
					loggedIn = false;
				//If user is logged on, send a query
				if(loggedIn && !exit){
					try{
						sendMessage(new Message(MessageType.QUERY_MESSAGES, ""),outToServer, false);
						
					}
					catch (IOException e){
						e.printStackTrace();
					}
				}
			}
		}
	} 
}


