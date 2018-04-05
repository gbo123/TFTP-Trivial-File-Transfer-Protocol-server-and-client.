//
// Created by βιμ on 13/01/2017.
//

#ifndef BOOST_ECHO_CLIENT_ENCODERDECODER_H
#define BOOST_ECHO_CLIENT_ENCODERDECODER_H
#include "Packet.h"
#include <iostream>
#include <vector>
using namespace std;
class MessageEncoderDecoder {



private :
	vector <char> bytes ;
	int len=0;
	short opcode=-1;
	short packetSize=0;
	void pushByte(char nextByte);
    Packet* RRQDecode();
    Packet* WRQDecode ();
    Packet* DataDecode ();
    Packet* ACKDecode ();
    Packet* ERRORDecode ();
    Packet* DIRQDecode ();
    Packet* LOGRQDecode ();
    Packet* DELRQDecode ();
    Packet* BCASTDecode ();
    Packet* DISCDecode ();




public:

    MessageEncoderDecoder();
    ~MessageEncoderDecoder();
    char* encode (Packet* message);

    Packet* DecodeNextByte(char nextByte);



};


#endif //BOOST_ECHO_CLIENT_ENCODERDECODER_H
