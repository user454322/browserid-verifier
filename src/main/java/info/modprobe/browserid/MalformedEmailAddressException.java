package info.modprobe.browserid;

/**
 * 
 * Using this class instead of {@code javax.mail.internet.AddressException}
 *
 */
public class MalformedEmailAddressException extends Exception {
	
	private static final long serialVersionUID = -8609916441584877393L;

	
	public MalformedEmailAddressException(){
		super();
	}
	
	public MalformedEmailAddressException(final String msg){
		super(msg);
	}
	
	public MalformedEmailAddressException(final Throwable throwable){
		super(throwable);
	}
	
	public MalformedEmailAddressException(final String msg, final Throwable throwable){
		super(msg,throwable);
	}
	
}
