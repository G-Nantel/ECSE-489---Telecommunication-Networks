public class Message {
    private MessageType messageType;
    private int subMessageType;
    private String data;

    public Message(MessageType messageType, int subMessageType, String data){
        this.messageType = messageType;
        this.subMessageType = subMessageType;
        this.data = data;
    }

    public Message(MessageType messageType, String data){
        this.messageType = messageType;
        this.subMessageType = 0;
        this.data = data;
    }

    public MessageType getMessageType(){
        return this.messageType;
    }

    public int getSubMessageType(){
        return this.subMessageType;
    }

    public String getData(){
        return this.data;
    }
}

