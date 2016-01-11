package wetsch.jbcsclient;



public enum ServerClientCommand{
	connectionOK("Connection to server is ok."),
	dataReceived("Data received by server.");
	
	private final String message;
	
	ServerClientCommand(String message){
		this.message = message;
	}

	String getmessage(){
		return message;
	}
}