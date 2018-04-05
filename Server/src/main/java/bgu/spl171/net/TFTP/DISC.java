package bgu.spl171.net.TFTP;

public class DISC extends Packet {

	public DISC() {
		opcode =10;
	}

	@Override
	public byte[] encode() {
		byte[] bytesArr = new byte [2];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    return bytesArr;
	}

}
