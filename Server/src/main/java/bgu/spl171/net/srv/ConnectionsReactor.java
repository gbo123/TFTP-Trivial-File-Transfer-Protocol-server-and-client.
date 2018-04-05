package bgu.spl171.net.srv;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl171.net.TFTP.Packet;
import bgu.spl171.net.api.bidi.Connections;

public class ConnectionsReactor  implements Connections<Packet> {
	private ConcurrentHashMap<Integer,NonBlockingConnectionHandler> map;
	
	
	
	public ConnectionsReactor() {
		map=new ConcurrentHashMap<Integer,NonBlockingConnectionHandler>();
	}

	@Override
	public boolean send(int connectionId, Packet msg) {
		NonBlockingConnectionHandler currentHandler =map.get(connectionId);
		if (currentHandler==null)
				return false;
		else{
			currentHandler.send(msg);
			return true;
		}
	}

	@Override
	public void broadcast(Packet msg) {
		for (int i=0; i<map.size(); i++){
			NonBlockingConnectionHandler currentHandler =map.get(i);
			if (currentHandler!=null)
				currentHandler.send(msg);
		}
		
	}

	@Override
	public void disconnect(int connectionId) {
		NonBlockingConnectionHandler currentHandler=map.get(connectionId);
		if (currentHandler!=null)
			map.remove(connectionId);
	}
	
	public void connect (int connectionId, NonBlockingConnectionHandler handler){
		map.put(connectionId,handler);
	}

}
