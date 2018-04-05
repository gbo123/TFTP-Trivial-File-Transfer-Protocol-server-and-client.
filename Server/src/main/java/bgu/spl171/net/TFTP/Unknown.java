package bgu.spl171.net.TFTP;

public class Unknown extends Packet {
	
	public Unknown() {
		opcode =11;
	}

	@Override
	public byte[] encode() {
		byte[] bytesArr = new byte [2];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    return bytesArr;
	}

}
