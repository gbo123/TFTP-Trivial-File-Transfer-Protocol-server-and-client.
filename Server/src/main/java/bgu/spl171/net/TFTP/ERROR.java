package bgu.spl171.net.TFTP;

public class ERROR extends Packet {
	private short errorCode;
	private String errMsg;

	public ERROR(short errorCode, String errMsg) {
		opcode =5;
		this.errorCode=errorCode;
		this.errMsg=errMsg;
	}

	
	
	public short getErrorCode() {
		return errorCode;
	}



	public void setErrorCode(short errorCode) {
		this.errorCode = errorCode;
	}



	public String getErrMsg() {
		return errMsg;
	}



	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}



	@Override
	public byte[] encode() {
		byte [] errorBytes = (errMsg+'\0').getBytes();
		byte[] bytesArr = new byte [errorBytes.length+4];
	    bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
	    bytesArr[1] = (byte)(opcode & 0xFF);
	    bytesArr[2] = (byte)((errorCode >> 8) & 0xFF);
	    bytesArr[3] = (byte)(errorCode & 0xFF);
	    for(int i=4; i<bytesArr.length; i++){
	    	bytesArr[i]=errorBytes[i-4];
	    }
	    return bytesArr;
	}

}
