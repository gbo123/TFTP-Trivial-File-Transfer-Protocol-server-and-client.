package bgu.spl171.net.TFTP;

public class DIRQ extends Packet {

	public DIRQ() {
		opcode =6;
	}

	@Override
	public byte[] encode() {
		byte[] bytesArr = new byte [2];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    return bytesArr;
	}

}
