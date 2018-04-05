package bgu.spl171.net.TFTP;

public class DELRQ extends Packet {
	private String fileName;
	
	public DELRQ(String fileName) {
		opcode =8;
		this.fileName=fileName;
	}

	public String getFileName(){
		return fileName;
	}
	
	@Override
	public byte[] encode() {
		byte [] fileBytes = (fileName+'\0').getBytes();
		byte[] bytesArr = new byte [fileBytes.length+2];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    for(int i=2; i<bytesArr.length; i++){
	    	bytesArr[i]=fileBytes[i-2];
	    }
	    return bytesArr;
		
	}

}
