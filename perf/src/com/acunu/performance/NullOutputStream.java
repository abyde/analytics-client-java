package com.acunu.performance;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

	@Override
	public void write(int arg0) throws IOException {
		// throw away
	}

}
