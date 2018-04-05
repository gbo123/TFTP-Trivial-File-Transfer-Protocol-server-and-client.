package bgu.spl171.net.TFTP;

public abstract class Packet {
	protected short opcode;
	
	
	public Packet() {
	}
	
	
	public abstract byte[] encode();
	
	public short getOpcode(){
		return opcode;
	}

	//abstract execute();
}
