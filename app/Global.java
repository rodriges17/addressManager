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
            //Ebean.save((List) Yaml.load("test-data1.yml"));
            
            User admin = new User("admin@test.com", "admin", true);
            admin.save();
            
            ContactGroup bern = new ContactGroup("Bern", admin);
            bern.save();
            ContactGroup basel = new ContactGroup("Basel", admin);
            basel.save();
            ContactGroup zurich = new ContactGroup("Zürich", admin);
            zurich.save();
            ContactGroup tessin = new ContactGroup("Tessin", admin);
            tessin.save();
            ContactGroup luzern = new ContactGroup("Luzern", admin);
            luzern.save();
            ContactGroup romande = new ContactGroup("Romande", admin);
            romande.save();
            ContactGroup stgallen = new ContactGroup("St. Gallen-Ostschweiz", admin);
            stgallen.save();
            ContactGroup fribourg = new ContactGroup("Fribourg", admin);
            fribourg.save();

            //Contact contact = new Contact("Sir", "Test", "John", "john.test@test.com", "Teststreet 3", "App1", "App2", "3012", "Test City", "012 345 6789", bern, true);
            //contact.save();
            Contact.create("Sir", "Test", "John", "john.test@test.com", "Teststreet 3", "App1", "App2", "3012", "Test City", "012 345 6789", "Bern", "ja");
            
        }
    }
}