package models;

import java.sql.Timestamp;
import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.Ebean;

import play.db.ebean.*;
import play.data.Form;
import play.data.validation.Constraints.*;
import play.data.format.*;

@Entity
public class Contact extends Model {
	
	@Id
	public Long id;
	
	@Required
	public String name;
	
	@Required
	public String firstName;
	
	public String title;
	
	@Email
	@Pattern(value="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	public String email;
	
	public String phone;
	
	public String street;
	
	public String appendix1;
	
	public String appendix2;
	
	public String zipcode;
	
	public String city;
	
	public String country;
	
	public Date createdAt;
	
	public Date lastEditedAt;
	
	public boolean isEdited = false;
	
	@Formats.DateTime(pattern="dd.MM.yyyy")
	public Date membershipSince;
	
	public String memberCategory;
	
	public boolean yearbookSubscription;

	@ManyToMany
	public List<ContactGroup> belongsTo = new LinkedList<ContactGroup>();

	  
	public static Finder<Long,Contact> find = 
			new Finder<Long,Contact>(Long.class, Contact.class);

	public Contact() {};
	
	public Contact(String title, String name, String firstName, String email,
			String street, String appendix1, String appendix2, String zipcode, String city, String phone, ContactGroup belongsTo, boolean yearbookSubscription) {
		this.title = title;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.appendix1 = appendix1;
		this.appendix2 = appendix2;
		this.zipcode = zipcode;
		this.city = city;
		this.phone = phone;
		this.createdAt = new Date();
		this.lastEditedAt = this.createdAt;
		this.belongsTo.add(belongsTo);	
		this.yearbookSubscription = yearbookSubscription;
	}
	
	public Contact(String title, String name, String firstName, String email,
			String street, String appendix1, String appendix2, String zipcode, String city, String phone, List<ContactGroup> belongingContactGroups, boolean yearbookSubscription) {
		this.title = title;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.appendix1 = appendix1;
		this.appendix1 = appendix2;
		this.zipcode = zipcode;
		this.city = city;
		this.phone = phone;
		this.createdAt = new Date();
		this.lastEditedAt = this.createdAt;
		for(int i = 0; i < belongingContactGroups.size(); i++){
			this.belongsTo.add(belongingContactGroups.get(i));
		}
		this.yearbookSubscription = yearbookSubscription;
			
	}
	
	public static Contact create(String title, String name, String firstName, String email,
			String street, String appendix1, String appendix2, String zipcode, String city, String phone, String belongsTo, String yearbook) {
		boolean yearbookSubscription = false;
		if(yearbook.contains("ja"))
			yearbookSubscription = true;
		if(belongsTo.contains("/")){
			String[] belongsToSplit = belongsTo.split("/");
			List<ContactGroup> belongingContactGroups = new LinkedList<ContactGroup>();
			for(int i = 0; i < belongsToSplit.length; i++){
				ContactGroup cg = ContactGroup.find.where().eq("name", belongsToSplit[i]).findUnique();
				belongingContactGroups.add(cg);
			}
			Contact contact = new Contact(title, name, firstName, email, street, appendix1, appendix2, zipcode, city, phone, belongingContactGroups, yearbookSubscription);
			contact.save();
			return contact;
		}
		else{
			ContactGroup cg = ContactGroup.find.where().eq("name", belongsTo).findUnique();
			Contact contact = new Contact(title, name, firstName, email, street, appendix1, appendix2, zipcode, city, phone, cg, yearbookSubscription);
			contact.save();
			return contact;
		}
	}

	public static List<Contact> all() {
		return find.all();
	}
	
	public static List<Contact> findInvolvingGroupOwner(String user) {
	       return find.fetch("belongsTo").where()
	                .eq("belongsTo.owners.email", user)
	           .findList();
	}
	
	public List<Contact> ofGroup(String groupName) {
		return find.where().eq(groupName, belongsTo).findList();
	}

	
	public static void delete(Long id) {
		find.ref(id).delete();
	}

	public void update(Form<Contact> updatedForm) {
		Contact updatedSource = updatedForm.get();
		this.name = updatedSource.name;
		this.firstName = updatedSource.firstName;
		this.title = updatedSource.title;
		this.email = updatedSource.email;
		this.phone = updatedSource.phone;
		this.street = updatedSource.street;
		this.city = updatedSource.city;
		this.country = updatedSource.country;
		//this.belongsTo = updatedSource.belongsTo;
		this.lastEditedAt = new Date();
		this.isEdited = true;
		this.membershipSince = updatedSource.membershipSince;
		this.memberCategory = updatedSource.memberCategory;
		this.yearbookSubscription = updatedSource.yearbookSubscription;

		this.save();	
	}

	public static Contact create(Contact contact) {
		contact.save();
		return contact;
	}

	public String toString() {
		return "Name: " + name + ", First Name: "
				+ firstName + ", Street: " + street
				+ ", City: " + city + ", Country: " + country
				+ ", Email: " + email
				+ ", Phone: " + phone + ", Group: " + belongsTo;
	}

	public static List<Contact> findEditedContacts() {
		// TODO Auto-generated method stub
		return find.where().eq("isEdited", true).findList();
	}
	
}
