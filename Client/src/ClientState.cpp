/*
 * ClientState.cpp
 *
 *  Created on: Jan 14, 2017
 *      Author: ayeletyi
 */

#include "ClientState.h"


ClientState::ClientState():expectingDirq(),expectingRrq(), expectingWrq(), expectingDisc() {
	expectingDirq=false;
	expectingRrq=false;
	expectingWrq=false;
	expectingDisc=false;


}

ClientState::~ClientState() {

}

bool ClientState:: getExpDirq(){
	return this->expectingDirq;
}

bool ClientState:: getExpRrq(){
	return this->expectingRrq;
}

bool ClientState:: getExpWrq(){
	return this->expectingWrq;
}

bool ClientState::getExpDisc()   {
    return this ->expectingDisc;
}

void ClientState:: setExpDirq(bool b){
	this->expectingDirq=b;
}

void ClientState:: setExpRrq(bool b){
	this->expectingRrq=b;
}

void ClientState:: setExpWrq(bool b){
	this->expectingWrq=b;
}
void ClientState:: setExpDisc(bool b){
	this->expectingDisc=b;
}
