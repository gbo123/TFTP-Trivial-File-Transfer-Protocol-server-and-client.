/*
 * ClientState.h
 *
 *  Created on: Jan 14, 2017
 *      Author: ayeletyi
 */

#ifndef SRC_CLIENTSTATE_H_
#define SRC_CLIENTSTATE_H_

class ClientState {
private:
	bool expectingDirq;
	bool expectingRrq;
	bool expectingWrq;
	bool expectingDisc;

public:
	ClientState();
	virtual ~ClientState();
	bool getExpDirq();
	bool getExpRrq();
	bool getExpWrq();
	bool getExpDisc();
	void setExpDirq(bool b);
	void setExpRrq(bool b);
	void setExpWrq(bool b);
	void setExpDisc(bool b);


};

#endif /* SRC_CLIENTSTATE_H_ */
