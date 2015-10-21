
import java.io.*;
import java.nio.ByteBuffer;
import java.net.*;

public final class test {
	
	private static Socket clientSocket;
 
	public static void main(String[] args) throws Exception {
		
		String URL = "ecse-489.ece.mcgill.ca";
		 int port = 5000;
		 
		 
		 BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

		 
		 //Set up the client socket
		 InetAddress serverAddress = InetAddress.getByName(URL);
		 Socket clientSocket = new Socket(serverAddress.getHostAddress(), port);

		 
		//create output stream
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		//create input stream
		BufferedInputStream inFromServer = new BufferedInputStream(clientSocket.getInputStream());
			
        //  welcome message
        System.out.println("Hi, you are now connected to our Chat platform");
		
		String username = "123", password = "123";
		
		 sendMessage(new Message(MessageType.CREATE_USER, "14,14"), outToServer);
		 sendMessage(new Message(MessageType.DELETE_USER, ""), outToServer);
		 try {
			Thread.sleep(700);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		
		//read response and save as message
		byte[] messageBytes = new byte[4];

			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();

			}
			int int1 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
			System.out.println(int1);
			
			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();
			}
			int int2 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
			System.out.println(int2);
			
			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();
			}
			int int3= messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
			System.out.println(int3);
			
			byte[] messageString = new byte[int3];
			for(int i = 0; i < int3; i++){
				messageString[i] = (byte) inFromServer.read();

			}
			String string = new String(messageString);
			System.out.println(string);

		 Message message = new Message(MessageType.getMessageType(int1), int2, string);
		 
		 System.out.println("Message type: " + message.getMessageType().getMessageTypeInt());
		 System.out.println("sub-Message type: " + message.getSubMessageType());
	  	 System.out.println("Data: " + message.getData());

	

			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();

			}
			int1 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//	System.out.println(int1);
			
			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();
			}
			int2 = messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//	System.out.println(int2);
			
			for(int i = 0; i < 4; i++){
				messageBytes[i] = (byte) inFromServer.read();
			}
			 int3= messageBytes[0]<< 24 | messageBytes[1]<< 16 |messageBytes[2]<< 8 | messageBytes[3] ;
		//	System.out.println(int3);
			
		messageString = new byte[int3];
			for(int i = 0; i < int3; i++){
				messageString[i] = (byte) inFromServer.read();

			}
			string = new String(messageString);
		//	System.out.println(string);

		 Message message1 = new Message(MessageType.getMessageType(int1), int2, string);
		 
		 System.out.println("Message type: " + message1.getMessageType().getMessageTypeInt());
		 System.out.println("sub-Message type: " + message1.getSubMessageType());
	  	 System.out.println("Data: " + message1.getData());
		 
	clientSocket.close(); 
	}
	
	public static void sendMessage(Message message, DataOutputStream outToServer) throws IOException{
		//Convert the data in bytes
		byte[] dataBytes = message.getData().getBytes();

		//Get the length of the dataBytes
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
		

	}

}