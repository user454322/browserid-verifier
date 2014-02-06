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
