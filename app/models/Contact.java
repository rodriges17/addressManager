package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.Form;
import play.data.validation.Constraints.*;

import com.avaje.ebean.*;

@Entity
public class Contact extends Model {
	
	@Id
	public Long id;
	
	@Required
	public String name;
	
	public String firstName;
	
	@Email
	@Pattern(value="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	public String email;
	
	public String street;
	
	public String city;
	
	public String phone;
	
	@ManyToOne
	@Required
	public ContactGroup belongsTo;
	  
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
		this.email = updatedSource.email;
		this.street = updatedSource.street;
		this.city = updatedSource.city;
		this.phone = updatedSource.phone;
		this.belongsTo = updatedSource.belongsTo;
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
