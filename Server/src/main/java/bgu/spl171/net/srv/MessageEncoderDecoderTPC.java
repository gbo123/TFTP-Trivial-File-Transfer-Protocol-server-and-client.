package bgu.spl171.net.srv;

import bgu.spl171.net.TFTP.*;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.StringJoiner;

public class MessageEncoderDecoderTPC  implements MessageEncoderDecoder<Packet> {

	private byte[] bytes= new byte [518];
	private int len=0;
	short opcode=-1;
	short packetSize=0;


	public MessageEncoderDecoderTPC() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] encode(Packet message) {	
		byte[] ans= message.encode();
		return ans;
		
	}





	public Packet decodeNextByte(byte nextByte) {

		pushByte(nextByte);

		if (opcode==-1){
			return null;
		}

		if (opcode==1){
			return RRQDecode();
		}

		if (opcode==2){
			return WRQDecode();
		}

		if (opcode==3){
			return DataDecode();
		}

		if (opcode==4){
			return ACKDecode();
		}

		if (opcode==5){
			return ERRORDecode();
		}

		if (opcode==6){
			return DIRQDecode();
		}

		if (opcode==7){
			return LOGRQDecode();
		}
		
		if (opcode==8){
			return DELRQDecode();
		}

		if (opcode==9){
			return BCASTDecode();
		}

		if (opcode==10){
			return DISCDecode();
		}
		else{
			len=0;
			opcode=-1;
			return (new Unknown());
		}
	}




	private void pushByte(byte nextByte) {
		bytes[len] = nextByte;
		len++;
		
		if(len==2){
			short result = (short)((bytes[0] & 0xff) << 8);
			result += (short)(bytes[1] & 0xff);
			opcode=result;
			if (result==-1){
				opcode=11;
			}
		}

	}

	private Packet RRQDecode (){

		if(bytes[len-1]=='\0'){
			String fileName=new String(bytes,2, len-3, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new RRQ(fileName);
		}

		else 
			return null;
	}
	private Packet WRQDecode (){
		if(bytes[len-1]=='\0'){
			String fileName=new String(bytes,2, len-3, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new WRQ(fileName);
		}

		else 
			return null;
	}
	private Packet DataDecode (){
		if (len==4) {
			packetSize= (short)((bytes[2] & 0xff) << 8);
			packetSize += (short)(bytes[3] & 0xff);

		}
		if (packetSize==len-6){
			short numOfBlocks= (short)((bytes[4] & 0xff) << 8);
			numOfBlocks += (short)(bytes[5] & 0xff);
			byte[] data = Arrays.copyOfRange(bytes,6,packetSize+6);
			len=0;
			opcode=-1;
			short ans=packetSize;
			packetSize=0;
			return new DATA(ans,numOfBlocks,data);
		}
		
		else 
			return null;
	}

	
	private Packet ACKDecode (){
		if(len==4){
			short numOfBlocks=(short)((bytes[2] & 0xff) << 8);
			numOfBlocks += (short)(bytes[3] & 0xff);
			len=0;
			opcode=-1;
			return new ACK(numOfBlocks);


		}
		else
			return null;

		}


	private Packet ERRORDecode (){
		if(bytes[len-1]=='\0' && len>3){
			short errorCode=(short)((bytes[2] & 0xff) << 8);
			errorCode += (short)(bytes[3] & 0xff);
			String errorMessage=new String(bytes,4, len-5, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new ERROR(errorCode,errorMessage);
		}

		else return null;

	}


	private Packet DIRQDecode (){
		len=0;
		opcode= -1;
		return new DIRQ();

	}

	private Packet LOGRQDecode (){
		if(bytes[len-1]=='\0'){
			String userName=new String(bytes,2, len-3, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new LOGRQ(userName);

		}

		else 
			return null;

	}
	private Packet DELRQDecode (){

		if(bytes[len-1]=='\0'){
			String fileName=new String(bytes,2, len-3, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new DELRQ(fileName);
		}

		else 
			return null;
	}

	private Packet BCASTDecode (){

		if(bytes[len-1]=='\0' && len!=3){
			byte delOrAdd= bytes[2];
			String fileName=new String(bytes,3, len-4, StandardCharsets.UTF_8);
			len=0;
			opcode=-1;
			return new BCAST(delOrAdd,fileName);
		}

		else 
			return null;

	}

	private Packet DISCDecode (){
		len=0;
		opcode= -1;
		return new DISC();

	}

}
