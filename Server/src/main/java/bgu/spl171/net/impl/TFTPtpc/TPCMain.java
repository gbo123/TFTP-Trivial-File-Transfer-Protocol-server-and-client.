package bgu.spl171.net.impl.TFTPtpc;

import java.util.function.Supplier;

import bgu.spl171.net.TFTP.BidiMessagingProtocolTPC;
import bgu.spl171.net.TFTP.Packet;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.BaseServer;
import bgu.spl171.net.srv.ConnectionHandlerTPC;
import bgu.spl171.net.srv.MessageEncoderDecoderTPC;
import bgu.spl171.net.srv.Server;

public class TPCMain<T> extends BaseServer<T>{
	
	public TPCMain(int port, Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encdecFactory) {
		super(port, protocolFactory, encdecFactory);
		
	}


	@Override
	protected void execute(ConnectionHandlerTPC handler) {
		new Thread(handler).start();
		
	}


	    public static void main(String[] args) {
	   
	    		System.out.println("start server");
	    		Supplier<BidiMessagingProtocol<Packet>> protocolFactory= ()->{return new BidiMessagingProtocolTPC();};
	    		
	    		Supplier<MessageEncoderDecoder<Packet>> encdecFactory= ()->{return new MessageEncoderDecoderTPC();};

	    	
	    	int port= Integer.parseInt(args[0]);
	    	BaseServer<Packet> server =new TPCMain<>(port, protocolFactory, encdecFactory);
	    	server.serve();
	 
		
	}




















		
}
