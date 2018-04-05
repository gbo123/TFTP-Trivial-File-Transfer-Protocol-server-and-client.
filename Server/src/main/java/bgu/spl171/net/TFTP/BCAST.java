package bgu.spl171.net.TFTP;

public class BCAST extends Packet {
	private byte delOrAdd;
	private String fileName;

	public BCAST(byte delOrAdd, String fileName) {
		opcode =9;
		this.delOrAdd=delOrAdd;
		this.fileName=fileName;
	}

	@Override
	public byte[] encode() {
		byte [] fileBytes = (fileName+'\0').getBytes();
		byte[] bytesArr = new byte [fileBytes.length+3];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    bytesArr[2]= delOrAdd;
	    for(int i=3; i<bytesArr.length; i++){
	    	bytesArr[i]=fileBytes[i-3];
	    }
	    return bytesArr;
	}

}
