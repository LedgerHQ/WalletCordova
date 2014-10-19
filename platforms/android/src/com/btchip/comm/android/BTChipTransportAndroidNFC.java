package com.btchip.comm.android;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.btchip.BTChipException;
import com.btchip.comm.BTChipTransport;
import com.btchip.utils.Dump;

public class BTChipTransportAndroidNFC implements BTChipTransport {
	
	public static final int DEFAULT_TIMEOUT = 5000;
	
	private IsoDep card;
	private int timeout;
	private boolean debug;	
	
	public BTChipTransportAndroidNFC(IsoDep card, int timeout) {
		this.card = card;
		this.timeout = timeout;
	}
	
	public BTChipTransportAndroidNFC(IsoDep card) {
		this(card, DEFAULT_TIMEOUT);
	}
	

	@Override
	public byte[] exchange(byte[] command) throws BTChipException {
		try {
			if (!card.isConnected()) {
				card.connect();
				card.setTimeout(timeout);
				if (debug) {
					Log.d(BTChipTransportAndroid.LOG_STRING, "Connected");
				}
			}
			if (debug) {
				Log.d(BTChipTransportAndroid.LOG_STRING, "=> " + Dump.dump(command));
			}
			byte[] commandLe = new byte[command.length + 1];
			System.arraycopy(command, 0, commandLe, 0, command.length);
			byte[] response = card.transceive(commandLe);
			if (debug) {
				Log.d(BTChipTransportAndroid.LOG_STRING, "<= " + Dump.dump(response));
			}
			return response;			
		}
		catch(Exception e) {
			try {
				card.close();
			}
			catch(Exception e1) {				
			}
			throw new BTChipException("I/O error", e);
		}
		
	}

	@Override
	public void close() throws BTChipException {
		try {
			if (card.isConnected()) {
				card.close();
			}			
		}
		catch(Exception e) {			
		}
	}

	@Override
	public void setDebug(boolean debugFlag) {
		this.debug = debugFlag;
	}
}
