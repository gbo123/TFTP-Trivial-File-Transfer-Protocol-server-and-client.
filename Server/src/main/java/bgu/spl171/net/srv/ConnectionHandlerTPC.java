package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.MessagingProtocol;
import bgu.spl171.net.TFTP.*;


public class ConnectionHandlerTPC implements Runnable, ConnectionHandler <Packet> {


    private final BidiMessagingProtocol protocol;
    private final MessageEncoderDecoder encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    
    
    public ConnectionHandlerTPC(Socket clientSock, MessageEncoderDecoder messageEncoderDecoder,
			BidiMessagingProtocol bidiMessagingProtocol) {
    	this.protocol= bidiMessagingProtocol;
    	this.encdec= messageEncoderDecoder;
    	this.sock=clientSock;
	}

	public void run() {

        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            while (!protocol.shouldTerminate()&& connected && (read = in.read()) >= 0) {
            	Packet nextMessage = (Packet) encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                   protocol.process(nextMessage);        
                }
            }
            if(protocol.shouldTerminate()){
                close();
             }

        } 
        catch (IOException ex) {
            	ex.printStackTrace();
        }

    }


    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(Packet msg) {
        byte[] bytes= encdec.encode(msg);
        try {
            out.write(bytes);           
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}