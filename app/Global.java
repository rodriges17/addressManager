import play.*;
import models.*;

public class Global extends GlobalSettings {
	
	@Override
    public void onStart(Application app) {
        // Check if there is no user in the database
        if (User.find.findRowCount() == 0) {          
            User admin = new User("admin@test.com", "admin", true);
            admin.save();
        }
        
        User admin = User.findByEmail("admin@test.com");

        // Check if there are no contact groups in the database
        if(ContactGroup.find.findRowCount() == 0) {
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
            ContactGroup stgallen = new ContactGroup("St. Gallen-Zürich", admin);
            stgallen.save();
            ContactGroup nichtmitglied = new ContactGroup("Nichtmitglied", admin);
            nichtmitglied.save();
        }

        // Check if there is no contact in the database
        if (Contact.find.findRowCount() == 0)
        	Contact.create("Sir", "Test", "John", "john.test@test.com", "Teststreet 3", "App1", "App2", "3012", "Test City", "Switzerland", "012 345 6789", "Bern", "ja", "Hier keine", "Schwizerdütsch");             
    }
}