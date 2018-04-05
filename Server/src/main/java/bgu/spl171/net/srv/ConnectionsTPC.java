package bgu.spl171.net.srv;

import bgu.spl171.net.TFTP.Packet;
import bgu.spl171.net.api.bidi.Connections;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsTPC implements Connections<Packet> {
	private ConcurrentHashMap<Integer,ConnectionHandlerTPC> map;
	
	
	
	public ConnectionsTPC() {
		map=new ConcurrentHashMap<Integer,ConnectionHandlerTPC>();
	}

	@Override
	public boolean send(int connectionId, Packet msg) {
		ConnectionHandlerTPC currentHandler =map.get(connectionId);
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
			ConnectionHandlerTPC currentHandler =map.get(i);
			if (currentHandler!=null)
				currentHandler.send(msg);
		}
		
	}

	@Override
	public void disconnect(int connectionId) {
		ConnectionHandlerTPC currentHandler=map.get(connectionId);
		if (currentHandler!=null)
			map.remove(connectionId);
	}
	
	public void connect (int connectionId, ConnectionHandlerTPC handler){
		map.put(connectionId,handler);
	}

}
