import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientCredentials implements Serializable{
    private String login;
    private byte[] hashPassword;

    ClientCredentials(String login, String password){
        this.login = login;
        try {
            this.hashPassword = MessageDigest.getInstance("MD5").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    public byte[] hashPassword() {
        return hashPassword;
    }
}
