/*
 * Packet.cpp
 *
 *  Created on: Jan 13, 2017
 *      Author: ayeletyi
 */

#include "Packet.h"
#include "../include/Packet.h"

using namespace std;

Packet::Packet(): opcode() {
	// TODO Auto-generated constructor stub

}

Packet::~Packet() {
	// TODO Auto-generated destructor stub
}

short Packet:: getOpcode() const{
	return this->opcode;
}

RRQ::RRQ(string fileName): fileName() {
	opcode=1;
	this->fileName=fileName;

}

string RRQ::getFileName() const{
	return this->fileName;
}

char* RRQ::encode(){
	char* bytesArr = new char[fileName.length()+3];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
	    for(unsigned int i=0; i<fileName.size(); i++){
		   	bytesArr[i+2]=fileName[i];
	    }
		   bytesArr[fileName.length()+2]='\0';
	    return bytesArr;
}


WRQ::WRQ(string fileName): fileName(){
	opcode=2;
	this->fileName=fileName;

}
string WRQ::getFileName() const{
	return this->fileName;
}
char* WRQ::encode(){
	char* bytesArr = new char[fileName.length()+3];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
	    for(unsigned int i=0; i<fileName.size(); i++){
		   	bytesArr[i+2]=fileName[i];
	    }
		   bytesArr[fileName.length()+2]='\0';
	    return bytesArr;
}



DATA::DATA(short size, short numOfBlock, vector<char> data): size(), numOfBlock(), data(){
	opcode=3;
	this->size=size;
	this->numOfBlock=numOfBlock;
	this->data=data;
}
/*
DATA::~DATA(){

	data.clear();

}
*/
short DATA::getSize(){
	return this->size;
}
short DATA::getNumOfBlock() const{
	return this->numOfBlock;
}
vector<char> DATA::getData(){
	return this->data;
}

char* DATA::encode(){
		char* bytesArr = new char[size+6];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
		bytesArr[2] = ((size >> 8) & 0xFF);
    	bytesArr[3] = (size & 0xFF);
		bytesArr[4] = ((numOfBlock >> 8) & 0xFF);
    	bytesArr[5] = (numOfBlock & 0xFF);
		for(int i=6; i<size+6; i++){
		    bytesArr[i]=data.at(i-6);
		}
		    return bytesArr;
}



ACK::ACK(short numOfBlock): numOfBlock(){
	opcode=4;
	this->numOfBlock=numOfBlock;
}
short ACK::getNumOfBlock() const{
	return this->numOfBlock;
}
char* ACK::encode(){
		char* bytesArr = new char[4];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
		bytesArr[2] = ((numOfBlock >> 8) & 0xFF);
    	bytesArr[3] = (numOfBlock & 0xFF);
		    return bytesArr;
}



ERROR::ERROR(short errorCode, string errMsg): errorCode(), errMsg(){
	opcode=5;
	this->errorCode=errorCode;
	this->errMsg=errMsg;
}
short ERROR::getErrorCode() const{
	return this->errorCode;
}
string ERROR::getErrMsg() const{
	return this->errMsg;
}
char* ERROR::encode(){
	char* bytesArr = new char[errMsg.length()+5];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
    	bytesArr[2] = ((errorCode >> 8) & 0xFF);
    	bytesArr[3] = (errorCode & 0xFF);
	    for(unsigned int i=0; i<errMsg.size(); i++){
		   	bytesArr[i+4]=errMsg[i];
	    }
		   bytesArr[errMsg.length()+4]='\0';
	    return bytesArr;
}


DIRQ::DIRQ() {
	opcode=6;
}
char* DIRQ::encode(){
		char* bytesArr = new char[2];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
		    return bytesArr;
}

LOGRQ::LOGRQ(string username): username(){
	opcode=7;
	this->username=username;
}
string LOGRQ::getName() const{
	return this->username;
}
char* LOGRQ::encode(){
	char* bytesArr = new char[username.length()+3];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
	    for(unsigned int i=0; i<username.size(); i++){
		   	bytesArr[i+2]=username[i];
	    }
		   bytesArr[username.length()+2]='\0';
	    return bytesArr;
}



DELRQ::DELRQ (string fileName): fileName() {
	opcode=8;
	this->fileName=fileName;
}
string DELRQ::getFileName() const{
	return this->fileName;
}
char* DELRQ::encode(){
	char* bytesArr = new char[fileName.length()+3];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
	    for(unsigned int i=0; i<fileName.size(); i++){
		   	bytesArr[i+2]=fileName[i];
	    }
		   bytesArr[fileName.length()+2]='\0';
	    return bytesArr;
}


BCAST::BCAST (char delOrAdd, string fileName): delOrAdd(), fileName(){
	opcode=9;
	this->delOrAdd=delOrAdd;
	this->fileName=fileName;
}
char BCAST::getDelOrAdd() const{
	return this->delOrAdd;
}
string BCAST::getFileName() const{
	return this->fileName;
}
char* BCAST::encode(){
	char* bytesArr = new char[fileName.length()+4];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
    	bytesArr[2] = delOrAdd;
	    for(unsigned int i=0; i<fileName.size(); i++){
		   	bytesArr[i+3]=fileName[i];
	    }
		   bytesArr[fileName.length()+3]='\0';
	    return bytesArr;
}


DISC::DISC(){
	opcode=10;
}
char* DISC::encode(){
		char* bytesArr = new char[2];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
		    return bytesArr;
}

Unknown::Unknown(){
	opcode=11;
}
char* Unknown::encode(){
		char* bytesArr = new char[2];
		bytesArr[0] = ((opcode >> 8) & 0xFF);
    	bytesArr[1] = (opcode & 0xFF);
		    return bytesArr;
}

