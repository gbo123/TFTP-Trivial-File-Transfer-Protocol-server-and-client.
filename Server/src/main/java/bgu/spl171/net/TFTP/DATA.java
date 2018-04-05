package bgu.spl171.net.TFTP;

public class DATA extends Packet {
	private short size;
	private short numOfBlock;
	private byte[] data;
	

	public DATA(short size, short numOfBlock, byte[] data) {
		opcode =3;
		this.size=size;
		this.numOfBlock=numOfBlock;
		this.data=data;
		
	}
	public short getSize(){
		return size;
	}
	public short getNumOfBlock(){
		return numOfBlock;
	}
	public byte[] getData(){
		return data;
	}
	

	@Override
	public byte[] encode() {
		byte[] bytesArr = new byte [data.length+6];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    bytesArr[2] = (byte)((size >> 8) & 0xFF);
	    bytesArr[3] = (byte)(size & 0xFF);
	    bytesArr[4] = (byte)((numOfBlock >> 8) & 0xFF);
	    bytesArr[5] = (byte)(numOfBlock & 0xFF);
	    for(int i=6; i<bytesArr.length; i++){
	    	bytesArr[i]=data[i-6];
	    }
	    return bytesArr;
	}

}
