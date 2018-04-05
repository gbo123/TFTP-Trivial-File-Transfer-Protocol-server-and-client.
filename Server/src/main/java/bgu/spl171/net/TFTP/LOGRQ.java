package bgu.spl171.net.TFTP;

public class LOGRQ extends Packet {
	private String username;
	
	public LOGRQ(String username) {
		opcode =7;
		this.username=username;		
	}
	
	public String getName(){
		return username;
	}

	@Override
	public byte[] encode() {	
		byte [] nameBytes = (username+'\0').getBytes();
		byte[] bytesArr = new byte [nameBytes.length+2];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    for(int i=2; i<bytesArr.length; i++){
	    	bytesArr[i]=nameBytes[i-2];
	    }
	    return bytesArr;
	}

}
