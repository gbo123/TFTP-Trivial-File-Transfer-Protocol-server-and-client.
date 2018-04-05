#include <stdlib.h>
#include <connectionHandler.h>
#include <iostream>
#include <fstream>
#include <boost/thread.hpp>
#include <boost/date_time.hpp>
#include <stdlib.h>
#include <string>
#include <boost/locale.hpp>
#include "../include/connectionHandler.h"
#include <stdlib.h>

using boost::asio::ip::tcp;


void readFromKeyboard(ConnectionHandler &connectionHandler){
	while (connectionHandler.isOpenForKeyboard()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        pair<Packet*,int> ans=connectionHandler.stringToPacket(line);
        const char* bytes= ans.first->encode();
        int size=ans.second;
        delete ans.first;
        bool sent=connectionHandler.sendBytes(bytes,size);
        delete []bytes;
        if (!sent) {
            break;
        }

    }

}



void listenToServer(ConnectionHandler &connectionHandler){

	while(connectionHandler.isOpenForServer()){
    	char* buf=new char[1];
    	while(connectionHandler.isOpenForServer()){
    		if (connectionHandler.getBytes( buf ,1 )){
    			Packet* nextMessage = connectionHandler.encdec.DecodeNextByte(buf[0]);
    		    if (nextMessage != 0) {
    		    	connectionHandler.process(nextMessage);
    		    	//delete nextMessage;
    		    }
    		}
    	}
    	delete []buf;
        buf=0;
    }
}



int main (int argc, char *argv[]) {
	 if (argc < 3) {
	        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
	        return -1;
	    }

	    std::string host = argv[1];
	    unsigned short port = atoi(argv[2]);

	    ConnectionHandler connectionHandler(host, port);

	    if (!connectionHandler.connect()) {
	        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
	        return 1;
	    }

			boost::thread stdinListener(readFromKeyboard, boost::ref(connectionHandler));
			boost::thread serverListener(listenToServer, boost::ref(connectionHandler));

			stdinListener.join();
			serverListener.join();

    return 0;
}

