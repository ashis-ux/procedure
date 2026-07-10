package com.bsp.procedure_gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class homeController {
	
	@GetMapping("/database/search")
	public String home() {
		log.info("recieved request");
		 return "database/database-search";
	}
	
	@GetMapping("/database/create")
	public String database_create() {
		log.info("recieved request");
		 return "database/database-create";
	}
	
	@GetMapping("/database/update/{id}")
	public String updateDatabasePage(
	        @PathVariable Long id,
	        Model model) {

	    model.addAttribute("databaseId", id);
	    return "database/database-update";
	}
	
	@GetMapping("/procedure/create")
	public String proceduremaster_create() {
		log.info("recieved request");
		 return "procedure-master/procedure-create";
	}
	
	@GetMapping("/procedure/search")
	public String proceduremaster_search() {
		log.info("recieved request");
		 return "procedure-master/procedure-search";
	}
	
	@GetMapping("/procedure/update/{id}")
	public String proceduremaster_update(
		  @PathVariable Long id,
	        Model model) {

	    model.addAttribute("databaseId", id);
		 return "procedure-master/procedure-update";
	}
	
	@GetMapping("/procedure/{procedureId}/parameters")
	public String viewParameters(

	        @PathVariable
	        Long procedureId,

	        Model model) {

	    model.addAttribute("procedureId", procedureId);

	    return "procedure-parameter/procedure-parameter";

	}
	
	@GetMapping("procedure/{procedureId}/parameter/create")
	public String createParameters(

	        @PathVariable
	        Long procedureId,

	        Model model) {

	    model.addAttribute("procedureId", procedureId);

	    return "procedure-parameter/parameter-create";

	}

}
