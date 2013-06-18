package models;

import play.data.Form;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;
import java.util.List.*;

@Entity
public class Contact extends Model {
	
	@Id
	public Long id;
	
	public String name;
	
	public String firstName;
	
	public String email;
	
	public String street;
	
	public String city;
	
	public String phone;
	
	public static Finder<Long, Contact> find = new Finder(Long.class, Contact.class);

    @ManyToOne
	public static ContactGroup belongsTo;
	
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
	
	public static Contact create(String name, String firstName, String email, String street, String city, String phone, String belongsTo) {
		Contact contact = new Contact(name, firstName, email, street, city, phone, ContactGroup.find.byId(belongsTo));
		contact.save();
		return contact;
	}

	public static List<Contact> all() {
		return find.all();
	}
	
	public static List<Contact> ofGroup(String groupName) {
		return find.where().eq(groupName, belongsTo).findList();
	}

	
	public static void delete(Long id) {
		find.ref(id).delete();
	}

	public void update(Long id, Form<Contact> updatedForm) {
		Contact toUpdate = find.ref(id);
		Contact updatedSource = updatedForm.get();
		System.out.println(updatedSource.name);
		toUpdate.name = updatedSource.name;
		toUpdate.firstName = updatedSource.firstName;
		toUpdate.email = updatedSource.email;
		toUpdate.street = updatedSource.street;
		toUpdate.city = updatedSource.city;
		toUpdate.phone = updatedSource.phone;
		toUpdate.save();	
	}

	public static void create(Contact contact) {
		contact.save();
		
	}
	
}
