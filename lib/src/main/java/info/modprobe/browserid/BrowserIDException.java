/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

public class BrowserIDException extends RuntimeException {

	
	private static final long serialVersionUID = 1380103006395531020L;

	public BrowserIDException(){
		
	}
	
	public BrowserIDException(final String msg){
		super(msg);
	}
	
	public BrowserIDException(final Throwable throwable){
		super(throwable);
	}
	
	public BrowserIDException(final String msg, final Throwable throwable){
		super(msg,throwable);
	}

}
