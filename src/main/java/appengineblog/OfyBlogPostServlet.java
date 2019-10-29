package appengineblog;


import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.User;

import com.google.appengine.api.users.UserService;

import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;


import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

 

public class OfyBlogPostServlet extends HttpServlet {
	
	static {
		ObjectifyService.register(BlogPost.class);
		ObjectifyService.register(Blog.class);
		ObjectifyService.register(Subscriber.class);
	}

    public void doPost(HttpServletRequest req, HttpServletResponse resp)

                throws IOException {

        UserService userService = UserServiceFactory.getUserService();

        User user = userService.getCurrentUser();
        
        ObjectifyService.register(BlogPost.class);

     
        String guestbookName = req.getParameter("guestbookName");
        
        String title = req.getParameter("title");
        
        if(title == null || title.equals("")){
        		title = "Untitled";
        }

        String content = req.getParameter("content");

        BlogPost blogpost = new BlogPost(user, title, content, guestbookName);

        ofy().save().entity(blogpost).now();
 
        resp.sendRedirect("/index.jsp?guestbookName=" + guestbookName);

    }

}

