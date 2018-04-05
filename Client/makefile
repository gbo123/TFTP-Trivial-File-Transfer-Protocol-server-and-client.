CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread

all: EchoClient
	g++ -o bin/echoExample bin/connectionHandler.o bin/ClientState.o bin/MessageEncoderDecoder.o bin/Packet.o bin/echoClient.o $(LDFLAGS) 

EchoClient: bin/connectionHandler.o bin/ClientState.o bin/MessageEncoderDecoder.o bin/Packet.o bin/echoClient.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp
	
bin/ClientState.o: src/ClientState.cpp
	g++ $(CFLAGS) -o bin/ClientState.o src/ClientState.cpp

bin/MessageEncoderDecoder.o: src/MessageEncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/MessageEncoderDecoder.o src/MessageEncoderDecoder.cpp
	
bin/Packet.o: src/Packet.cpp
	g++ $(CFLAGS) -o bin/Packet.o src/Packet.cpp
	
bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
