
// Note that in order to use enum, our compiler must have compliance level 1.5 or higher
// if not, enum is not recognized ! 

	public enum MessageType {
		EXIT, BADLY_FORMATTED_MESSAGE, ECHO, LOGIN, LOGOFF, CREATE_USER, DELETE_USER, CREATE_STORE,
		SEND_MESSAGE, QUERY_MESSAGES;
	

    //Returns the ordinal of this enumeration constant 
    // this means it returns its position in its enum declaration,
    //where the initial constant is assigned an ordinal of zero
    public int getMessageTypeInt(){
        return (this.ordinal()+20);
    }

    public static MessageType getMessageType(int messageTypeInt){
        return MessageType.values()[messageTypeInt-20];
    }
	
}
