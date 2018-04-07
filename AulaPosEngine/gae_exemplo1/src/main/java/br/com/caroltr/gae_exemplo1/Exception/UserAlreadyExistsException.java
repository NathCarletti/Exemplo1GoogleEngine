package br.com.caroltr.gae_exemplo1.Exception;

public class UserAlreadyExistsException extends Exception	{
    private	String	message;
    public	UserAlreadyExistsException(String	message) {
        super(message);
        this.message	=	message;
    }
    @Override
    public	String	getMessage() {return	message;}
}
