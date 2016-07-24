package wetsch.jbcsserver.server.listeners;


/*
 * Last modified 7/2/2016
 */

/**
 * This class implements all the methods for the JbcsServerListener.
 * Use this adapter if you wish to only implement only certain methods.
 */
public abstract class JbcsServerListenerAdapter implements JbcsServerListener{

	@Override
	public void serverStarted(ServerEvent e) {}

	@Override
	public void ServerStopped(ServerEvent e) {}

	@Override
	public void barcodeServerDatareceived(BarCoderEvent e) {}

	@Override
	public void serverConsole(String message) {	}

}
