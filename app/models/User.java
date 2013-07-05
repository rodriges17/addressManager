package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
@Table(name="cm_users")
public class User extends Model {
	
	@Id
	@Constraints.Required
    @Formats.NonEmpty
    public String email;
	
	@Constraints.Required
    public String password;
	
    public boolean isAdmin;

//    @ManyToMany
//    public List<ContactGroup> administratedContactGroups = new LinkedList<ContactGroup>();
    
    public User(String email, String password, boolean isAdmin) {
      this.email = email;
      this.password = password;
      this.isAdmin = isAdmin;
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);
    
    public static User authenticate(String email, String password) {
        return find.where().eq("email", email)
            .eq("password", password).findUnique();
    }
    
    /**
     * Retrieve a User from email.
     */
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }
    
	public String toString() {
		return "User [email=" + email + ", password=" + password + ", isAdmin="
				+ isAdmin + "]";
	}

	public static void create(User user) {
		user.save();	
	}

}
