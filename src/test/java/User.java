import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//This Object was created to practice deserialization

public class User {
    @JsonProperty
    private String Email;
    @JsonProperty
    private String Password;
    @JsonProperty
    private String FullName;

    public User(){};
    public User(String mail, String psw, String name){
        Email = mail;
        Password = psw;
        FullName = name;
    }

    
    
    

    
}
