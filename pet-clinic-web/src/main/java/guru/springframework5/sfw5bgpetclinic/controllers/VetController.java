//***************************************************************************
//Class:  VetController 
//
//Primary Controller for vet related pages (identified by @Controller / not extension).
//
//When an HTTP request arrives at server, will be handled by Spring provided 
//DispatcherServlet (front controller) that uses Handler Mapping to determine 
//which Controller to invoke (i.e. VetController, etc.).  
//
//@RequestMapping is used to map the HTTP Request Path to specific method on 
//the controller (for instance, can say all URL for that Web App ending with
///hello will go to ("/hello") method on controller which might just return a 
//view name "hello." Recall when jsp, ViewResolver added prefix/suffix to 
//tell where to find file and to add ".jsp."  When Thymeleaf, default looks in 
//templates and finds hello.html.
//
//Key requirements:
//#1 - @Controller - Tell Spring this is a Spring MVC Controller to be instantiated
//#2 - @RequestMapping - Inform Spring this is a controller method that can be invoked
//                      to handle specific incoming HTML request.
//*************************************************************************** 
package guru.springframework5.sfw5bgpetclinic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller      // #1 - Tell Spring this is a Spring MVC Controller to be instantiated. 
public class VetController {

	// #2 - Tell Spring this is a Controller method to handle HTTP requests.
	//      Specifically, this method handles if user ends URL with /vets, /vets/index or /vets/index.html.
	//      If you run the app will see RequestMappingHandlerMapping list these 4 types handled.
	//
	//      - Request vets from service to add to Model.  
    //      - Returns core name of Thymeleaf template. 
    @RequestMapping({"/vets", "/vets", "/vets/index", "/vets/index.html"})
	public String listVets() {
    	return "vets/index";  // Spring looks in templates in vets folder for index.html since Thymeleaf. 
    	                      // If JSP, ViewResolver would provide pre/suffix to build full jsp file/path. 
    }
    
}  // end class VetController

