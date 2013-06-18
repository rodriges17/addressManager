package models;

import play.db.ebean.*;
import javax.persistence.*;
import com.avaje.ebean.*;

import play.data.validation.Constraints.Required.*;
import play.db.ebean.Model.*;

import javax.persistence.*;
import java.util.*;
import java.util.List.*;

@Entity
public class User extends Model {
	
	@Id
    public String email;
    public String password;

//    @ManyToMany
//    public LinkedList<ContactGroup> administratedContactGroups;
    
    public User(String email, String password) {
      this.email = email;
      this.password = password;
//      this.administratedContactGroups = new LinkedList<ContactGroup>();
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);
    
    public static User authenticate(String email, String password) {
        return find.where().eq("email", email)
            .eq("password", password).findUnique();
    }
    
    public Object getId() {
        return email;
    }
    
    protected void setId_(Object id) {
        email = (String) processId_(id);
    }
    
    protected static Object processId_(Object id) {
        return id.toString();
    }

}
