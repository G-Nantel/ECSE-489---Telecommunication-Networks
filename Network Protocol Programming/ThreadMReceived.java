import java.io.*;

public class ThreadMReceived extends Thread{
    private BufferedInputStream inFromServer;
 
    public ThreadMReceived(BufferedInputStream inFromServer){
        this.inFromServer = inFromServer;
    }
	
	public void run() {
		
		
        while(true){
			
			try{
				while(inFromServer.available() > 0){
					Message message = Client.receiveMessage(inFromServer);
					if(!(message.getMessageType().getMessageTypeInt() == MessageType.QUERY_MESSAGES.getMessageTypeInt() && (message.getSubMessageType() == 0 || message.getSubMessageType() == 2))){	 
						if(message.getMessageType().getMessageTypeInt() == MessageType.QUERY_MESSAGES.getMessageTypeInt() && message.getSubMessageType() == 1){
						String sign = message.getData();
						String[] s=sign.split(",", 3);				
						System.out.println("\n****MESSAGE RECEIVED AT " +s[1] +"****");
						System.out.println(">>>> "+s[0]+": " +s[2] );
						System.out.println("\nType a command. To see the list of all commands, type 'All'\n");
						 Client.ServerResponded = message;
						}else{
							System.out.println("***** from Server: "+message.getData());
							Client.ServerResponded = message;
						}
					} 
				}
			} catch (IOException e){
				System.out.println(e);
				System.out.println("*****Exiting******");
				System.exit(1);
			}
        }
   }
    
}   