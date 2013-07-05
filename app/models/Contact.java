package models;

import java.sql.Timestamp;
import java.util.*;

import javax.persistence.*;

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
	
	public String city;
	
	public String country;
	
	@ManyToOne
	@Required
	public ContactGroup belongsTo;
	
	public Date createdAt;
	
	public Date lastEditedAt;
	
	@Formats.DateTime(pattern="dd.MM.yyyy")
	public Date membershipSince;
	
	public String memberCategory;
	
	public boolean yearbookSubscription;
	  
	public static Finder<Long,Contact> find = 
			new Finder<Long,Contact>(Long.class, Contact.class);

	public Contact(String name, String firstName, String email,
			String street, String city, String phone, ContactGroup belongsTo) {
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.street = street;
		this.city = city;
		this.phone = phone;
		this.belongsTo = belongsTo;
		this.createdAt = new Date();
		this.lastEditedAt = this.createdAt;
	}
	
	public static Contact create(String name, String firstName, String email,
			String street, String city, String phone, String belongsTo) {
		ContactGroup cg = ContactGroup.find.where().eq("name", belongsTo).findUnique();
		System.out.println(cg.name);
		Contact contact = new Contact(name, firstName, email, street, city, phone, cg);
		contact.save();
		System.out.println(contact.belongsTo);
		return contact;
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
		this.belongsTo = updatedSource.belongsTo;;
		this.lastEditedAt = new Date();
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
				+ ", City: " + city + ", Email: " + email
				+ ", Phone: " + phone + ", Group: " + belongsTo;
	}
	
}
