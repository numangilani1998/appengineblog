package appengineblog;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class CronEmailServlet extends HttpServlet {
	static final long DAY = 86400000; // number of ms in a day 
	public boolean inLastDay(Date date) {
	    return date.getTime() > System.currentTimeMillis() - DAY;
	}
	private void sendMail(String email, String emailContent) {

	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    try {
	    	_logger.info("Start Mail Service");
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress("admin@testapp.appspotmail.com", "admin"));
	      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, "subscriber"));
	      msg.setSubject("Thanks for subscribing");
	      msg.setText(emailContent);
	      Transport.send(msg);
	    } catch (Exception e) {
	      _logger.info("ERROR");
	    }

	}
	
	
	
private static final Logger _logger = Logger.getLogger(CronEmailServlet.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		ObjectifyService.register(Subscriber.class);
		ObjectifyService.register(BlogPost.class);
		
		List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
		
		List<BlogPost> blogPosts = ObjectifyService.ofy().load().type(BlogPost.class).list();
		
		Collections.sort(blogPosts);
		Collections.reverse(blogPosts);
		
		String emailContent = "";
		
		
		for(BlogPost bp : blogPosts) {
			if(inLastDay(bp.getDateObj())) {
				emailContent += bp.toString();
			}
		}
		
		
		if(!emailContent.equals("")) {
			
			for(Subscriber s: subscribers) {
		
				try {
					_logger.info("Email sent to " + s.getEmail());
					sendMail(s.getEmail(), emailContent);
				}
				catch (Exception ex) {
					_logger.info("Error in for loop");
				}
			}
		}

		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	
	
}

