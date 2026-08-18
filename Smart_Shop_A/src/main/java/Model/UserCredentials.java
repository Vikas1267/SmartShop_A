package Model;

import java.security.PrivateKey;

public class UserCredentials {
	private User user;
	private String passwordHash;
	private String passwordSalt;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getPasswordSalt() {
		return passwordSalt;
	}
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
	public UserCredentials(User user, String passwordHash, String passwordSalt) {
		super();
		this.user = user;
		this.passwordHash = passwordHash;
		this.passwordSalt = passwordSalt;
	}
	
}