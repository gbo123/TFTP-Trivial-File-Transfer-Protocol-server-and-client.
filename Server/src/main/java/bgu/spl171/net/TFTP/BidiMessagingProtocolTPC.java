package bgu.spl171.net.TFTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.ConnectionHandlerTPC;
import bgu.spl171.net.srv.ConnectionsTPC;
import bgu.spl171.net.srv.State;

import java.util.concurrent.ConcurrentLinkedDeque;

public class BidiMessagingProtocolTPC implements BidiMessagingProtocol<Packet> {
	private Connections connections;
	private int clientId;
	private boolean shouldTerminate;
	private static ConcurrentHashMap<Integer,String> loggedUsers=new ConcurrentHashMap<Integer,String>();
	private ConcurrentLinkedDeque<DATA> readDataPackets;
	private ConcurrentLinkedDeque<DATA> writeDataPackets;
	private String path="";
	private State state;
	
	public BidiMessagingProtocolTPC() {
		shouldTerminate=false;
		readDataPackets=new ConcurrentLinkedDeque<DATA>();
		writeDataPackets=new ConcurrentLinkedDeque<DATA>();
		state=new State();
	}

	@Override
	public void start(int connectionId, Connections connections) {
		this.connections= connections;
		clientId=connectionId;
		
	}

	@Override
	public void process(Packet message) {
		
		if(message.getOpcode()==1){
			
			if (isLoggedin()){
			String file=((RRQ)message).getFileName();
			boolean exist=false;
			File folder = new File ("Files");
			String [] fileNames=folder.list(); //get a list of the filenames from the folder
			for (int i=0; i<fileNames.length; i++){ //check if the file already exist
				if (fileNames[i].equals(file)){
					exist=true;
					break;
				}
			}
			
			if(exist){
				state.setSendRRQ(true);
				Path filePath = Paths.get("Files/"+file);
				try {
					byte[] fileBytes = Files.readAllBytes(filePath);
					for (int i=0; i<=fileBytes.length; i=i+512){ //creates data packets and adds them to the queue
						byte[] currentData;
						DATA currentPacket;
						if (fileBytes.length-i<512){ //the last packet
							
							currentData=Arrays.copyOfRange(fileBytes, i, fileBytes.length);
							currentPacket=new DATA ((short)(fileBytes.length-i),(short)(i/512+1), currentData);
							
							readDataPackets.add(currentPacket); //add the packet to the queue
							state.incDataRRQCounter();
						}
						else{
							currentData=Arrays.copyOfRange(fileBytes, i, i+512);
							currentPacket=new DATA ((short)(512),(short)(i/512+1), currentData);
							readDataPackets.add(currentPacket); //add the packet to the queue
							state.incDataRRQCounter();
						}
						
					}
					state.setSendRRQ(true);
					connections.send(clientId, readDataPackets.poll());
					//set the counter of expected data packets
					
			
				} catch (IOException e) {
					e.printStackTrace();
					Packet error=new ERROR ((short) 2, "Access violation");
					connections.send(clientId, error);
				}

			}
			else{
				Packet error=new ERROR ((short) 1, "File not found");
				connections.send(clientId, error);
			}
			
		}
		}
		
		
		if(message.getOpcode()==2){

			if (isLoggedin()) {
				path = ((WRQ) message).getFileName();
				boolean exist = false;
				File folder = new File("Files");
				String[] fileNames = folder.list(); // get a list of the
													// filenames from the folder
				for (int i = 0; i < fileNames.length; i++) { // check if the
																// file already
																// exist
					if (fileNames[i].equals(path)) {
						exist = true;
						break;
					}
				}

				if (exist) {
					Packet error = new ERROR((short) 5, "File already exists");
					connections.send(clientId, error);

				}

				else {
					state.setExpWRQ(true);
					ACK ack = new ACK((short) 0);
					connections.send(clientId, ack);

				}
			}
		}
		
		
		if(message.getOpcode()==3){
			
			if (isLoggedin()){
			
			if (state.isExpWRQ()){
			//System.out.println("number of block "+((DATA)message).getNumOfBlock());
			writeDataPackets.add((DATA)message);
			ACK ack=new ACK (((DATA)message).getNumOfBlock());
			connections.send(clientId, ack);
			
				if(((DATA)message).getData().length<512){
				File newFile=new File ("Files/"+path);
				try {
					newFile.createNewFile();
					FileOutputStream output=new FileOutputStream("Files/"+path);      
					while (writeDataPackets.isEmpty()==false){
						output.write(writeDataPackets.poll().getData());
					}
				state.setExpWRQ(false);
				BCAST bcast=new BCAST ((byte)'\0', path);
				Enumeration<Integer> users =loggedUsers.keys();
				while (users.hasMoreElements()){ //sends the broadcast message to all logged-in users
					connections.send(users.nextElement(), bcast);
				}
				} 
				catch (IOException e) {
					writeDataPackets.clear();
					ERROR error=new ERROR((short)0, "file was already written");
					connections.send(clientId, error);
					e.printStackTrace();
				}

				}
			}
			else{
				ERROR error=new ERROR((short)6, "not expecting data packet"); //if client is not suppose to send a data packet
				connections.send(clientId, error);
			}
		}
		}
		
		if(message.getOpcode()==4){
			
			if (isLoggedin()){
				if(state.isSendDirq()){
					//System.out.println("expecting ack from dirq");
					if(!readDataPackets.isEmpty()){
						connections.send(clientId, readDataPackets.poll());
						state.decDataDIRQCounter();
					}
					if (state.getDataDIRQCounter()==0)
						state.setSendDirq(false);
				}
				else if(state.isSendRRQ()){
					if(!readDataPackets.isEmpty()){
						DATA data=readDataPackets.poll();
						//System.out.println("datasize"+data.getSize());
						connections.send(clientId, data);
						state.decDataRRQCounter();
					}
					if (state.getDataRRQCounter()==0)
						state.setSendRRQ(false);
				}
				else{
					ERROR error=new ERROR((short)0, "not expecting ack packet"); //if not, throw an error
					connections.send(clientId, error);
				}
				
			}
			
			
		}
		if(message.getOpcode()==5){
			isLoggedin();
		}
		if(message.getOpcode()==6){
			
			if (isLoggedin()){
			File folder=new File("Files");
			String []fileNames=folder.list();	
			String answer="";
			for(int i=0; i<fileNames.length; i++){ //create a string with all the filenames
				answer=answer+fileNames[i]+ '\0';
			}

			byte[] stringBytes=answer.getBytes();
			
			if(stringBytes.length==0){ //the case in which the folder is empty
				DATA data = new DATA((short)0,(short)1,stringBytes);
				connections.send(clientId, data);
			}
			
			for (int i=0; i<stringBytes.length; i=i+512){ //creates data packets and adds them to the queue
				byte[] currentData;
				DATA currentPacket;
				if (stringBytes.length-i<512){ //the last packet
					currentData=Arrays.copyOfRange(stringBytes, i, stringBytes.length);
					currentPacket=new DATA ((short)(stringBytes.length-i),(short)(i/512+1), currentData);
					readDataPackets.add(currentPacket); //add the packet to the queue
					
				}
				else{
					currentData=Arrays.copyOfRange(stringBytes, i, i+512);
					currentPacket=new DATA ((short)(i+512),(short)(i/512), currentData);
					readDataPackets.add(currentPacket); //add the packet to the queue
					state.incDataDIRQCounter();
				}

			}
			state.setSendDirq(true);
			connections.send(clientId, readDataPackets.poll());
			}
		}
		
		if(message.getOpcode()==7){
			//check if clientId already exists
			
			if (loggedUsers.containsKey(clientId)){ //check if user is logged in
				ERROR error=new ERROR((short)0, "User logging in twice"); //if he's already connected, throw an error
				connections.send(clientId, error);
			}
			
			else{
				if(loggedUsers.contains(((LOGRQ) message).getName())){ //check if username already exist
					Packet error=new ERROR ((short) 7, "user already logged in");
					connections.send(clientId, error);
				}
				else{
					loggedUsers.put(clientId, ((LOGRQ) message).getName());
					Packet ack=new ACK((short)0);
					connections.send(clientId, ack);
				}
			}
		}
		if(message.getOpcode()==8){
			
			if (isLoggedin()){
			String file=((DELRQ)message).getFileName();
			boolean exist=false;
			File folder = new File ("Files");
			String [] fileNames=folder.list(); //get a list of the filenames from the folder
			for (int i=0; i<fileNames.length; i++){ //check if the file already exist
				if (fileNames[i].equals(file)){
					exist=true;
					break;
				}
			}
			
			if(exist){
				File fileDel =new File ("Files/"+file);
				fileDel.delete();
				BCAST bcast=new BCAST ((byte)'\0', path);
				Enumeration<Integer> users =loggedUsers.keys();
				while (users.hasMoreElements()){ //sends the broadcast message to all logged-in users
					connections.send(users.nextElement(), bcast);
				}

				
			}
			else{
				Packet error=new ERROR ((short) 1, "File not found");
				connections.send(clientId, error);
			}
			}	
		}
		if(message.getOpcode()==9){
			
		}
		if(message.getOpcode()==10){
			
			if (isLoggedin()){
			
			loggedUsers.remove(clientId);
			shouldTerminate=true;
			ACK ack = new ACK ((short)0);
			connections.send(clientId, ack);
			
			}
		}
		
		if(message.getOpcode()==11){
			Packet error=new ERROR ((short) 4, "Illegal TFTP operation");
			connections.send(clientId, error);
		}
	}

	@Override
	public boolean shouldTerminate() {
		if(shouldTerminate){
			connections.disconnect(clientId);		
		}

		return shouldTerminate;
	}
	
	public boolean isLoggedin(){
		if (!(loggedUsers.containsKey(clientId))){ //check if user is logged in
			ERROR error=new ERROR((short)6, "User not logged in"); //if not, throw an error
			connections.send(clientId, error);
			return false;
		}
		else
			return true;

	}


}
