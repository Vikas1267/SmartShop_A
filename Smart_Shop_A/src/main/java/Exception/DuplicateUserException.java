package Exception;

public class DuplicateUserException extends RuntimeException {

	public DuplicateUserException(String message) {
		super(message);
	}
}
