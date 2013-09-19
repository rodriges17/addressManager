package controllers;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import play.api.Play;
import play.data.*;
import play.mvc.*;
import models.*;
import views.html.*;
import util.pdf.PDF;

import utils.PDFiText;
import utils.PoiExcelFileReader;


/**
 * @author administrator
 *
 */
public class Application extends Controller {

	private static final User ANONYMOUS = new User("anon@ymous.com", "nopass", false);

	static Form<Contact> contactForm = Form.form(Contact.class);
	static Form<User> userForm = Form.form(User.class);
	static Form<ContactGroup> contactGroupForm = Form.form(ContactGroup.class);

	public static Result login(){
		return ok(views.html.login.render(Form.form(Login.class), getCurrentUser()));
	}

	public static Result authenticate() {
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm, getCurrentUser()));
		} else {
			session().clear();
			session("email", loginForm.get().email);
			flash("success", "You successfully logged in as " + loginForm.get().email);
			return redirect(
					routes.Application.contacts()
					);
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result logout(){
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.login());
	}

	@Security.Authenticated(Secured.class)
	public static Result index() {
		return redirect(routes.Application.contacts());
	}

	
	/**
	 * Lists all the contacts, where the logged in user
	 * is owner of the corresponding contact group
	 */
	@Security.Authenticated(Secured.class)
	public static Result contacts() {
		User user = getCurrentUser();
		return ok(
				views.html.index.render(Contact.findInvolvingGroupOwner(user.email), contactForm, user)    		
				);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editedContacts() {
		User user = getCurrentUser();
		return ok(
				views.html.editedContacts.render(Contact.findEditedContacts(), user)    		
				);
	}
	
	
	/**
	 * Generates a pdf file of all the contacts, 
	 * where the logged in user is owner of the
	 * corresponding contact group
	 */
	@Security.Authenticated(Secured.class)
	public static Result pdfSummary() {
		User user = getCurrentUser();
		return PDF.ok(views.html.pdfSummary.render(Contact.findInvolvingGroupOwner(user.email)));
    }
	
	@Security.Authenticated(Secured.class)
	public static Result pdfLabels() {
		User user = getCurrentUser();
		PDFiText.generateLabels(Contact.findInvolvingGroupOwner(user.email));
		response().setContentType("application/pdf");
		//response().setHeader("Content-disposition","attachment; filename=labels.pdf");
		return ok(new File("output/labels.pdf"));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result readFile() {
		PoiExcelFileReader.readFile();
		return redirect(routes.Application.contacts());
	}

	private static User getCurrentUser() {
		String currentId = request().username();
		User current; 
		if(currentId == null)
			current = ANONYMOUS;
		else {
			current = User.find.byId(request().username());
		}
		return current;
	}

	@Security.Authenticated(Secured.class)
	public static Result newContact() {

		Form<Contact> filledForm = contactForm.bindFromRequest();

		String name = filledForm.data().get("name");
		String firstName = filledForm.data().get("firstName");
		String title = filledForm.data().get("title");
		String email = filledForm.data().get("email");
		String street = filledForm.data().get("street");
		String city = filledForm.data().get("city");
		String phone = filledForm.data().get("phone");
		String memberCategory = filledForm.data().get("memberCategory");
		

		Contact newContact = new Contact();
		newContact.name = name;
		newContact.firstName = firstName;
		newContact.title = title;
		newContact.email = email;
		newContact.street = street;
		newContact.city = city;
		newContact.phone = phone;
		newContact.memberCategory = memberCategory;

		for(int j = 0; j < ContactGroup.options().size(); j++){
			String item = "belongsTo[" + j + "]";
			if(filledForm.data().get(item) != null){
				ContactGroup cg = ContactGroup.find.byId((long) Integer.parseInt(filledForm.data().get(item)));
				System.out.println(cg);
				newContact.belongsTo.add(cg);
			}
		}
			newContact.createdAt = new Timestamp(new Date().getTime());
			newContact.lastEditedAt = newContact.createdAt;
			
			//Contact.create(filledForm.get());
			
			//flash("success", "Contact " + filledForm.get().name + " has been created");
			//return redirect(routes.Application.contacts());  

		

		newContact.save();

		flash("success", "Contact " + newContact + " has been created");
		return redirect(routes.Application.contacts());  
	}

	@Security.Authenticated(Secured.class)
	public static Result deleteContact(Long id) {
		Contact.delete(id);
		return redirect(routes.Application.contacts());
	}

	@Security.Authenticated(Secured.class)
	public static Result updateContact(Long id) {
		Form<Contact> updatedForm = contactForm.bindFromRequest();
		if(updatedForm.hasErrors()) {
			return badRequest(
					views.html.index.render(Contact.all(), updatedForm, getCurrentUser())
					);
		} else {
			Contact.find.byId(id).update(updatedForm);
			flash("success", "Contact " + updatedForm.get().name + " has been updated");
			return redirect(routes.Application.contacts());  
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result viewContact(Long id) {
		String username = request().username();
		User current = User.find.byId(username);
		return ok(views.html.contactView.render(Contact.find.byId(id), current));
	}

	@Security.Authenticated(Secured.class)
	public static Result editContact(Long id) {
		Contact contact = Contact.find.byId(id);
		contactForm = contactForm.fill(contact);
		return ok(views.html.edit.render(contactForm, contact, getCurrentUser()));
	}

	@Security.Authenticated(Secured.class)
	public static Result add() {
		//DynamicForm dForm = new DynamicForm();
		return ok(views.html.add.render(contactForm, getCurrentUser(), ContactGroup.all()));

	}

	@Security.Authenticated(Secured.class)
	public static Result addUser() {
		if(getCurrentUser().isAdmin == false)
			return redirect(routes.Application.contacts());
		Form<User> userForm = Form.form(User.class);
		return ok(views.html.addUser.render(userForm, getCurrentUser(), User.find.all()));
	}

	@Security.Authenticated(Secured.class)
	public static Result newUser() {
		Form<User> filledForm = userForm.bindFromRequest();
		if(filledForm.hasErrors()) {
			System.out.println(filledForm.errors().toString());
			flash("error", "Please correct your entries");
			return badRequest(
					views.html.addUser.render(filledForm, getCurrentUser(), User.find.all())
					);
		} else {
			User.create(filledForm.get());
			flash("success", "User " + filledForm.get().email + " has been created");
			return redirect(routes.Application.contacts());  
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result addContactGroup() {
		// isAdmin now checked in the template
		//		if(getCurrentUser().isAdmin == false)
		//			return redirect(routes.Application.contacts());
		Form<ContactGroup> contactGroupForm = Form.form(ContactGroup.class);
		return ok(views.html.addContactGroup.render(contactGroupForm, getCurrentUser(), ContactGroup.find.all()));
	}

	// Automatic binding of owner is still missing at the moment
	@Security.Authenticated(Secured.class)
	public static Result newContactGroup() {
		Form<ContactGroup> filledForm = contactGroupForm.bindFromRequest();

		if(filledForm.hasErrors()) {
			System.out.println(filledForm.errors().toString());
			flash("error", "Please correct your entries");
			return badRequest(
					views.html.addContactGroup.render(filledForm, getCurrentUser(), ContactGroup.find.all())
					);
		} else {

			ContactGroup.create(filledForm.get());
			flash("success", "ContactGroup " + filledForm.get().name + " has been created");
			if(User.findByEmail(request().username()).isAdmin) {
				// manual binding of owner
				ContactGroup.find.ref(filledForm.get().id).addOwner(User.findByEmail(request().username()));
			}
			return redirect(routes.Application.contacts());  
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result addOwner(Long id) {
		DynamicForm form = Form.form().bindFromRequest();
		Collection<String> newOwners = form.data().values();
		ContactGroup group = ContactGroup.find.byId(id);
		
		Iterator it = newOwners.iterator();
		while(it.hasNext()){
			String userEmail = (String) it.next();
			group.addOwner(User.findByEmail(userEmail));
		}
		
		flash("success", newOwners.toString() + " is / are now owner of the group " + group.name);
		return ok(views.html.addContactGroup.render(contactGroupForm, getCurrentUser(), ContactGroup.find.all()));
	}

	@Security.Authenticated(Secured.class)
	public static Result showContactGroup(Long id) {
		ContactGroup cg = ContactGroup.find.ref(id);
		List<Contact> groupContacts = cg.contacts;
		List<User> groupOwners = cg.owners;
		List<User> allUsers = User.find.all();
		return ok(views.html.contactGroupView.render(cg, groupContacts, groupOwners, allUsers));
	}

	public static class Login {

		public String email;
		public String password;

		public String validate() {
			if (User.authenticate(email, password) == null) {
				return "Invalid user or password";
			}
			return null;
		}

	}

}