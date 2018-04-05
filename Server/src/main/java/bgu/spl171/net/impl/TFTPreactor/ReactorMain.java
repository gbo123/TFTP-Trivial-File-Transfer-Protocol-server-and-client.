package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.TFTP.BidiMessagingProtocolTPC;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.MessageEncoderDecoderTPC;
import bgu.spl171.net.srv.Server;

public class ReactorMain {
	public static void main (String[] args){
		int port= Integer.parseInt(args[0]);
		System.out.println("start server");
		Server.reactor(16, port,
				()->{return new BidiMessagingProtocolTPC();}, 
				()->{return new MessageEncoderDecoderTPC();}
				
				).serve();
	}
	


}
