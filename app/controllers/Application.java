package controllers;

import play.data.*;
import play.mvc.*;
import models.*;
import views.html.*;


public class Application extends Controller {
	
	private static final User ANONYMOUS = new User("anon@ymous.com", "nopass", false);
	
	static Form<Contact> contactForm = Form.form(Contact.class);
	
	public static Result login(){
		return ok(views.html.login.render(Form.form(Login.class)));
	}
	
	public static Result authenticate() {
	    Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
	    if (loginForm.hasErrors()) {
	        return badRequest(views.html.login.render(loginForm));
	    } else {
	        session().clear();
	        session("email", loginForm.get().email);
	        return redirect(
	            routes.Application.index()
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
    
	@Security.Authenticated(Secured.class)
    public static Result contacts() {
    	User user = getCurrentUser();
    		
    	return ok(
    		views.html.index.render(Contact.all(), contactForm, user)
    	);
    }

	private static User getCurrentUser() {
		String currentId = request().username();
    	User current; 
    	if(currentId == null)
    		current = ANONYMOUS;
    	else {
			current = User.find.byId(request().username());
		}
    	System.out.println(currentId);
    	return current;
	}

	@Security.Authenticated(Secured.class)
    public static Result newContact() {
    	Form<Contact> filledForm = contactForm.bindFromRequest();
    	  if(filledForm.hasErrors()) {
    	    return badRequest(
    	      views.html.index.render(Contact.all(), filledForm, getCurrentUser())
    	    );
    	  } else {
    	    Contact.create(filledForm.get());
    	    return redirect(routes.Application.contacts());  
    	  }
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
  	    Contact.find.byId(id).update(id, updatedForm);
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
		contactForm = Form.form(Contact.class);
    	return ok(views.html.add.render(contactForm, getCurrentUser()));
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