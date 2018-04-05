package bgu.spl171.net.srv;

public class State {
	private boolean sendRRQ;
	private boolean sendDirq;
	private boolean expWRQ;
	private int dataRRQCounter;
	private int dataDIRQCounter;
	

	public State() {
		sendRRQ=false;
		sendDirq=false;
		expWRQ=false;
		dataRRQCounter=0;
		dataRRQCounter=0;
	}


	public boolean isSendRRQ() {
		return sendRRQ;
	}


	public boolean isSendDirq() {
		return sendDirq;
	}


	public boolean isExpWRQ() {
		return expWRQ;
	}


	public int getDataRRQCounter() {
		return dataRRQCounter;
	}


	public int getDataDIRQCounter() {
		return dataDIRQCounter;
	}


	public void setSendRRQ(boolean sendRRQ) {
		this.sendRRQ = sendRRQ;
	}


	public void setSendDirq(boolean sendDirq) {
		this.sendDirq = sendDirq;
	}


	public void setExpWRQ(boolean expWRQ) {
		this.expWRQ = expWRQ;
	}


	public void incDataRRQCounter() {
		this.dataRRQCounter++;
	}


	public void incDataDIRQCounter() {
		this.dataDIRQCounter++;
	}
	
	public void decDataRRQCounter() {
		this.dataRRQCounter--;
	}


	public void decDataDIRQCounter() {
		this.dataDIRQCounter--;

	}
	
	
	
	
	
	

}
