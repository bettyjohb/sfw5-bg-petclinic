
// Key steps:
// #1 - @Controller - Tell Spring this is a Spring MVC Controller to be instantiated
// #2 - @RequestMapping - Inform Spring this is a controller method that can be invoked
//                        to handle specific incoming HTML request.

//***************************************************************************
//Class:  IndexController 
//
//Controller for index page (identified by @Controller / not extension).
//
//When an HTTP request arrives at server, will be handled by Spring provided 
//DispatcherServlet (front controller) that uses Handler Mapping to determine 
//which Controller to invoke (i.e. IndexController, etc.).  
//
//@RequestMapping is used to map the HTTP Request Path to specific method on 
//the controller (for instance, can say all URL for that Web App ending with
///hello will go to ("/hello") method on controller which might just return a 
//view name "hello." Recall when jsp, ViewResolver added prefix/suffix to 
//tell where to find file and to add ".jsp."  When Thymeleaf, default looks in 
//templates and finds hello.html. 
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @return String - core name of Thymeleaf template - looks in src/main/resources/templates by default
 *
 */
@Controller      // #1 - Tell Spring this is a Spring MVC Controller to be instantiated. 
public class IndexController {

	// #2 - Tell Spring this is a Controller method to handle HTTP requests.
	//      Specifically, this method handles if user ends URL with empty string, /, index, or index.html)
	//      If you run the app will see RequestMappingHandlerMapping list these 4 types handled.
	@RequestMapping({"", "/","index","index.html"})	 
	public String index() {
		return ("index");       // Spring looks in src/main/resources in templates by default for index.html
	}
}  // end IndexController
