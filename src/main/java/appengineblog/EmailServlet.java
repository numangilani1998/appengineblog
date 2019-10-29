package appengineblog;


import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.User;

import com.google.appengine.api.users.UserService;

import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

 

public class EmailServlet extends HttpServlet {
	
	
	private static final Logger _logger = Logger.getLogger(EmailServlet.class.getName());
    
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        UserService userService = UserServiceFactory.getUserService();

        User user = userService.getCurrentUser();
        
        ObjectifyService.register(Subscriber.class);

        String willSubscribe = req.getParameter("willSubscribe");
        
        _logger.info("Adding Subscriber " + user.getEmail());
        Subscriber subscriber = new Subscriber(user.getEmail());
        
        List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
        
        if(willSubscribe.equals("sub")) {
        		int nameTaken = 0;
        		for(Subscriber s:subscribers) {
        			if(s.getEmail().equals(subscriber.getEmail())) {
        				nameTaken = 1;
        			}
        		}
        		if(nameTaken==0){
        			ofy().save().entity(subscriber).now();
        		}
        		
        }else {
        		for(Subscriber s:subscribers) {
        			if(s.getEmail().equals(subscriber.getEmail())) {
        				ofy().delete().entity(s);
        			}
        		}
        }
 
        resp.sendRedirect("/index.jsp");

    }

}
