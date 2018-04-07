package br.com.caroltr.gae_exemplo1.Exception;

public class UserNotFoundException extends Exception	{
    private	String	message;
    public	UserNotFoundException(String	message) {
        super(message);
        this.message	=	message;
    }
    public	String	getMessage() {
        return	message;
    }
}
