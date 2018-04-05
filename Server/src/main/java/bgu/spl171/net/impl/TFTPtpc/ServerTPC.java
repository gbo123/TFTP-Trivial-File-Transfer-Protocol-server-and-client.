package bgu.spl171.net.impl.TFTPtpc;

import java.io.IOException;
import java.util.function.Supplier;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.BaseServer;
import bgu.spl171.net.srv.ConnectionHandlerTPC;
import bgu.spl171.net.srv.Server;

public class ServerTPC<T> implements Server<T> {

	  private final int port;
	    private final Supplier<BidiMessagingProtocol> protocolFactory;
	    private final Supplier<MessageEncoderDecoder> encdecFactory;
	
	
	
	 public ServerTPC( 

	
			 int port,
	            Supplier<BidiMessagingProtocol> protocolFactory,
	            Supplier<MessageEncoderDecoder> encdecFactory) {
		 this.port = port;
	        this.protocolFactory = protocolFactory;
	        this.encdecFactory = encdecFactory;
	 
	     
	    }

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serve() {
		// TODO Auto-generated method stub
		
	}

	
	
}