#include <connectionHandler.h>
#include <iostream>
#include <fstream>

#include "connectionHandler.h"
#include "ClientState.h"
#include "MessageEncoderDecoder.h"
#include "Packet.h"

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using namespace std;

ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), state(), dirq(), file(), uploadedFile(), readDataPackets(), writeDataPackets(),encdec(){}

ConnectionHandler::~ConnectionHandler() {
	while(!readDataPackets.empty()){
		delete (readDataPackets.front());
		readDataPackets.pop();
	}
	while(!readDataPackets.empty()){
		delete (readDataPackets.front());
		readDataPackets.pop();
	}

	close();

}

MessageEncoderDecoder ConnectionHandler::getEncdec() const{
	return this->encdec;
}

bool ConnectionHandler::isOpenForServer() const{
    return this->openForServer;
}
bool ConnectionHandler::isOpenForKeyboard ()const{
    return this->openForKeyboard;
}

bool ConnectionHandler::connect() {
    try {
    	tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
    	boost::system::error_code error;
    	socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {

    	while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);

    	}
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        //std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
        do{
            getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
       // std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    bool result=sendBytes(frame.c_str(),frame.length());
    if(!result) return false;
    return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }

}

//check if the client's directory contains a certain file
bool is_file_exist(const char *fileName)
{
    std::ifstream infile(fileName);
    return infile.good();
}


pair<Packet*,int> ConnectionHandler::stringToPacket(string command){

	if(command.substr(0,5)=="LOGRQ"){
		pair<Packet*, int> ans = pair<Packet*, int> ();

		if(command.size()>6 && command[5]==' '){
			string username= command.substr(6);
			Packet* newPacket=new LOGRQ(username);
			ans.first=newPacket;
			ans.second=username.length()+3;
		}

		else{
			Packet* unknown=new Unknown();
			ans.first=unknown;
			ans.second=2;
		}
		return ans;

	}



	else if(command.substr(0,5)=="DELRQ"){
		pair<Packet*, int> ans = pair<Packet*, int> ();
		if(command.size()>6 && command[5]==' '){
			string fileName= command.substr(6);
			Packet* newPacket=new DELRQ(fileName);
			ans.first=newPacket;
			ans.second=fileName.length()+3;

		}

		else{
			Packet* unknown=new Unknown();
			ans.first=unknown;
			ans.second=2;

		}
		return ans;
	}

	else if(command.substr(0,3)=="RRQ"){
		pair<Packet*, int> ans = pair<Packet*, int> ();
		if(command.size()>4 && command[3]==' '){
			file= command.substr(4);
			Packet* newPacket=new RRQ(file);
			ans.first=newPacket;
			ans.second=file.length()+3;
			state.setExpRrq(true);
		}

		else{
			Packet* unknown=new Unknown();
			ans.first=unknown;
			ans.second=2;
		}
		return ans;

	}

	else if(command.substr(0,3)=="WRQ"){

		pair<Packet*, int> ans = pair<Packet*, int>();
		if (command.size() > 4 && command[3] == ' ') {
			string fileName = command.substr(4);
			uploadedFile = fileName;
			const char *cstr = fileName.c_str();

			if (is_file_exist(cstr)) {
				Packet* newPacket = new WRQ(fileName);
				ans.first = newPacket;
				ans.second = fileName.length() + 3;
				state.setExpWrq(true);

				ifstream infile(fileName, ifstream::binary);
				infile.seekg(0, infile.end);
				long size = infile.tellg();
				infile.seekg(0);
				char* buffer = new char[size];
				infile.read(buffer, size);
				unsigned int index = 0;

				while (size >= 0) {
					if (size >= 512) {
						vector<char> data;
						for (unsigned int i = 0; i < 512; i++, index++) {
							data.push_back(buffer[index]);
						}
						DATA* dataPacket = new DATA(512, index / 512, data);
						writeDataPackets.push(dataPacket);
						size = size - 512;
					}
					else { //last data packet

						vector<char> data;
						for (unsigned int i = 0; i < size + 1; i++, index++) {
							data.push_back(buffer[index]);
						}

						DATA* dataPacket = new DATA(size, index / 512 + 1,
								data);
						writeDataPackets.push(dataPacket);
						size = size - 512;
						delete []buffer;


					}
				}
				 infile.close();
			}
			else{
				Packet* unknown=new Unknown();
				ans.first=unknown;
				ans.second=2;
			}

		}

		else{
			Packet* unknown=new Unknown();
			ans.first=unknown;
			ans.second=2;
		}
		return ans;
	}


	else if(command.substr(0,4)=="DIRQ"){
		pair<Packet*, int> ans = pair<Packet*, int> ();
		if(command.size()==4){
			Packet* newPacket=new DIRQ();
			ans.first=newPacket;
			ans.second=2;
			state.setExpDirq(true);
		}
		else{
			Packet* unknown=new Unknown();
			ans.first=unknown;
			ans.second=2;
		}
		return ans;
	}


	else if(command.substr(0,4)=="DISC"){
		pair<Packet*, int> ans = pair<Packet*, int> ();
			if(command.size()==4){
				Packet* newPacket=new DISC();
				ans.first=newPacket;
				ans.second=2;
				state.setExpDisc(true);
				openForKeyboard=false;

			}
			else{
				Packet* unknown=new Unknown();
				ans.first=unknown;
				ans.second=2;
			}
			return ans;
	}

	else{

		pair<Packet*, int> ans = pair<Packet*, int> ();
		Packet* unknown=new Unknown();
		ans.first=unknown;
		ans.second=2;
		return ans;
	}


}

void ConnectionHandler::process(Packet* nextMessage){
	short opcode=nextMessage->getOpcode();

	if (opcode == 9) {
		BCAST* bcast = dynamic_cast<BCAST*>(nextMessage);
		string filename = bcast->getFileName();
		char delOrAdd = bcast->getDelOrAdd();
		if (delOrAdd == '\0'){
			cout<<"BCAST del "<<filename<<endl;
		}
		if (delOrAdd == '\1'){
			cout<<"BCAST add "<<filename<<endl;
		}

	}

	if (opcode == 5) {
		ERROR* error = dynamic_cast<ERROR*>(nextMessage);
		short number = error->getErrorCode();
		if (number == 5) { //if file already exists, remove all the data packets from the queue
			while (!writeDataPackets.empty()) {
				writeDataPackets.pop();
			}
		}
		cout << "Error " << number << endl;

	}

	if (opcode == 4) {
		ACK* ack = dynamic_cast<ACK*>(nextMessage);
		short number = ack->getNumOfBlock();
		cout << "ACK " << number << endl;
		if (state.getExpWrq()) {
			if(!writeDataPackets.empty()){
				DATA* data = writeDataPackets.front();
				writeDataPackets.pop();
				short size = data->getSize();
				char* bytes = data->encode();
				sendBytes(bytes, size + 6);
				delete data;
				delete []bytes;

			}
			else{
			state.setExpWrq(false);
			cout<<"WRQ "<<uploadedFile<<" complete"<<endl;
			}
		}
		if(state.getExpDisc()){
			openForServer=false;
		}


	}

	if (opcode == 3) {
		DATA* data = dynamic_cast<DATA*>(nextMessage);
		if (state.getExpDirq()) {
			vector<char> currentData=data->getData();

            for(unsigned int i=0; i<currentData.size();i++){

                dirq=dirq+currentData.at(i);
            }

			if (data->getSize() < 512) { //checks if this is the last packet
				state.setExpDirq(false);
				stringstream ss(dirq);
				string singleFile;
				cout<<dirq<<endl;
				while (getline(ss,singleFile, '\0')) //seperate filenames
				{
				    cout<< singleFile <<endl;
				}

			}
		}
		if (state.getExpRrq()) {
			readDataPackets.push(data);
			//cout<<"queue size"<<readDataPackets.size()<<endl;
			if (data->getSize() < 512) { //checks if this is the last packet
				state.setExpRrq(false);
				ofstream outfile(file, ofstream::binary);
				while (!(readDataPackets.empty())) {
					DATA* d=readDataPackets.front();
					readDataPackets.pop();


					vector<char> data=d->getData();
					char* c=new char[d->getSize()+1];
					for(int i=0; i<d->getSize(); i++){
						c[i]=data.at(i);
					}
					short size=d->getSize();
					outfile.write(c,size);
					delete [] c;
					//data.clear();


				/*	const char* c=&*data.begin();
		            outfile.write(c,d->getSize());
					delete [] c;

					*/
				}
				outfile.close();
				cout<<"RRQ "<<file<<" complete"<<endl;
			}
		}
		ACK* ack = new ACK(data->getNumOfBlock());
		char* bytes = ack->encode();

		sendBytes(bytes, 4);
		delete ack;
		delete []bytes;



	}
	delete nextMessage;
	}












