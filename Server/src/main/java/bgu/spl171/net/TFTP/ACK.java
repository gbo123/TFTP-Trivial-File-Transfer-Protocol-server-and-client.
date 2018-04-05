package bgu.spl171.net.TFTP;

public class ACK extends Packet {
	private short numOfBlock;
	
	public ACK(short numOfBlock) {
		opcode=4;
		this.numOfBlock=numOfBlock;
	}

	@Override
	public byte[] encode() {
		byte[] bytesArr = new byte [4];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    bytesArr[2] = (byte)((numOfBlock >> 8) & 0xFF);
	    bytesArr[3] = (byte)(numOfBlock & 0xFF);
	    return bytesArr;
	
	}

}
