/*
 * Packet.h
 *
 *  Created on: Jan 13, 2017
 *      Author: ayeletyi
 */

#ifndef PACKET_H_
#define PACKET_H_
#include <iostream>
#include <vector>

using namespace std;

class Packet {
protected:
	short opcode;
public:
	Packet();
	virtual ~Packet();
	short getOpcode() const;
	virtual char* encode()=0;
};

class RRQ : public Packet{
	private:
	string fileName;

	public:
		RRQ (string fileName);
		string getFileName() const;
		virtual char* encode() override;
};

class WRQ : public Packet{
	private:
	string fileName;

	public:
		WRQ (string fileName);
		string getFileName() const;
		virtual char* encode() override;

};

class DATA : public Packet{
	private:
		short size;
		short numOfBlock;

	    vector<char> data;

    public:
		DATA (short size, short numOfBlock, vector<char> data);
		//~DATA();
		short getSize();
		short getNumOfBlock() const;
		vector<char> getData();
		virtual char* encode() override;
};

class ACK : public Packet{
	private:
		short numOfBlock;
	public:
		ACK(short numOfBlock);
		short getNumOfBlock() const;
		virtual char* encode() override;

};

class ERROR : public Packet{
	private:
		short errorCode;
		string errMsg;
	public:
		ERROR(short errorCode, string errMsg);
		short getErrorCode() const;
		string getErrMsg() const;
		virtual char* encode() override;
};

class DIRQ : public Packet{

	public:
		DIRQ();
		virtual char* encode() override;
};

class LOGRQ : public Packet{
	private:
	string username;
	public:
	LOGRQ(string username);
	string getName() const;
	virtual char* encode() override;
};

class DELRQ : public Packet{
	private:
		string fileName;
	public:
		DELRQ (string fileName);
		string getFileName() const;
		virtual char* encode() override;
};

class BCAST : public Packet{
	private:
		char delOrAdd;
		string fileName;
	public:
		BCAST(char delOrAdd, string fileName);
		char getDelOrAdd() const;
		string getFileName() const;
		virtual char* encode() override;

};

class DISC : public Packet{

	public:
	DISC();
	virtual char* encode() override;




};

class Unknown : public Packet{

	public:
	Unknown();
	virtual char* encode() override;




};

#endif


