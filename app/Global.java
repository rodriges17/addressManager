import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;

import models.*;

public class Global extends GlobalSettings {

	@Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (User.find.findRowCount() == 0) {
            //Ebean.save((List) Yaml.load("test-data.yml"));
            
            User admin = new User("admin@test.com", "admin", true);
            admin.save();
            
            ContactGroup bern = new ContactGroup("Bern", admin);
            bern.save();
            ContactGroup basel = new ContactGroup("Basel", admin);
            basel.save();
            
            Contact contact = new Contact("John", "Test", "john.test@test.com", "Teststreet 3", "Test City", "012 345 6789", bern);
            contact.save();
            
        }
    }
}