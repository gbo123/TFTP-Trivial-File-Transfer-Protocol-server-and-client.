//
// Created by βιμ on 13/01/2017.
//

#include "MessageEncoderDecoder.h"
#include <cstring>

using namespace std;

MessageEncoderDecoder::MessageEncoderDecoder(): bytes(), len(0), opcode(-1), packetSize(0) {

}

char* MessageEncoderDecoder :: encode(Packet* message) {

char* ans= message->encode();// need to check

return ans;
}

MessageEncoderDecoder :: ~MessageEncoderDecoder(){

}

Packet* MessageEncoderDecoder ::DecodeNextByte(char nextByte) {


    pushByte(nextByte);

    if (opcode==-1){
        return 0;
    }

    if (opcode==1){
        return RRQDecode();
    }

    if (opcode==2){
        return WRQDecode();
    }

    if (opcode==3){
        return DataDecode();
    }

    if (opcode==4){
        return ACKDecode();
    }

    if (opcode==5){
        return ERRORDecode();
    }

    if (opcode==6){
        return DIRQDecode();
    }

    if (opcode==7){
        return LOGRQDecode();
    }

    if (opcode==8){
        return DELRQDecode();
    }

    if (opcode==9){
        return BCASTDecode();
    }

    if (opcode==10){
        return DISCDecode();
    }
    else
    	return 0;
}



void MessageEncoderDecoder:: pushByte(char nextByte) {


    bytes.push_back(nextByte);
    len++;
    if(len==2){
        short result = (short)((bytes.at(0) & 0xff) << 8);
        result += (short)(bytes.at(1) & 0xff);
        opcode=result;
        if (result==-1){
            opcode=11;
        	len=0;
        	bytes.clear();
        }
    }

}

Packet* MessageEncoderDecoder ::RRQDecode (){

    if(bytes.at(len-1)=='\0'){

        string fileName="";
        for(int i=2; i<len-1; i++){
            fileName= fileName+bytes.at(i);
        }
        len=0;
        opcode=-1;
        bytes.clear();
        return new RRQ(fileName);
    }

    else
        return 0;
}


Packet* MessageEncoderDecoder:: WRQDecode (){

	if(bytes.at(len-1)=='\0'){
        string fileName="";
        for(int i=2; i<len-1; i++){
            fileName= fileName+bytes.at(i);
        }
        len=0;
        opcode=-1;
        bytes.clear();
        return new WRQ(fileName);
    }

    else
        return 0;
}

Packet* MessageEncoderDecoder::DataDecode (){
    if (len==4) {
        packetSize= (short)((bytes.at(2) & 0xff) << 8);
        packetSize += (short)(bytes.at(3) & 0xff);

    }
    if (packetSize==len-6){
        short numOfBlocks= (short)((bytes.at(4) & 0xff) << 8);
        numOfBlocks += (short)(bytes.at(5) & 0xff);
        string gago=string(bytes.begin()+6,bytes.end());

        vector<char> bib;

        for(unsigned int i=0; i<gago.length();i++){

            bib.push_back((char &&) gago.at(i));


        }

        len=0;
        opcode=-1;
        bytes.clear();
        short ans=packetSize;
        packetSize=0;
        DATA* newPacket=new DATA(ans,numOfBlocks,bib);
        //delete c;
        return newPacket;
    }

    else
        return 0;
}




 Packet*  MessageEncoderDecoder:: ACKDecode (){
    if(len==4){
        short numOfBlocks=(short)((bytes.at(2) & 0xff) << 8);
        numOfBlocks += (short)(bytes.at(3) & 0xff);
        len=0;
        opcode=-1;
        bytes.clear();
        return new ACK(numOfBlocks);


    }
    else
        return 0;

}


Packet* MessageEncoderDecoder:: ERRORDecode (){
	if(bytes.at(len-1)=='\0' && len>3){

        short errorCode=(short)((bytes.at(2) & 0xff) << 8);
        errorCode += (short)(bytes.at(3) & 0xff);
        string errorMessage="";
        for (int i=4;i<len-4; i++){
        	errorMessage= errorMessage+bytes.at(i);
        }
        len=0;
        opcode=-1;
        bytes.clear();
        return new ERROR(errorCode,errorMessage);
    }

    else return 0;

}


Packet* MessageEncoderDecoder:: DIRQDecode (){
    len=0;
    opcode= -1;
    bytes.clear();
    return new DIRQ();

}

Packet* MessageEncoderDecoder::LOGRQDecode (){
	if(bytes.at(len-1)=='\0'){
		string userName="";
        for(int i=2; i<len-2;i++){
        	userName=userName+ bytes.at(i);
        }
        len=0;
        opcode=-1;
        bytes.clear();
        return new LOGRQ(userName);

    }

    else return 0 ;

}
Packet* MessageEncoderDecoder:: DELRQDecode (){

	if(bytes.at(len-1)=='\0'){
		string fileName="";
		for(int i=2; i<len-1; i++){
		   fileName= fileName+bytes.at(i);
		}
		len=0;
		opcode=-1;
		bytes.clear();
        return new DELRQ(fileName);
    }

    else
        return 0;
}

Packet* MessageEncoderDecoder:: BCASTDecode (){

	if(bytes.at(len-1)=='\0'&& len!=3){
        char delOrAdd= bytes.at(2);
        string fileName="";
        for(int i=3; i<len-1; i++){
        	fileName= fileName+bytes.at(i);
        }
        len=0;
        opcode=-1;
        bytes.clear();
        return new BCAST(delOrAdd,fileName);
    }

    else
        return 0;

}

 Packet* MessageEncoderDecoder:: DISCDecode (){
    len=0;
    opcode= -1;
    bytes.clear();
    return new DISC();

}


