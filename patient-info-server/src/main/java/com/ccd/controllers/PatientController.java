package com.ccd.controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ccd.models.Patient;
import com.ccd.services.PatientService;
import com.ccd.validators.PatientValidator;

@RestController
@RequestMapping("/patient")
public class PatientController {

	private PatientService patientService;
	private static final Logger logger = Logger.getLogger(PatientService.class);
	
	@Autowired
	public PatientController(PatientService patientService){
		this.patientService = patientService;
	}
	
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
	    binder.addValidators(new PatientValidator());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient bodyPatient){
	    // logger.info("FLAG - got into the post method");
	    Patient savedPatient = this.patientService.add(bodyPatient);
	    
	    return new ResponseEntity<Patient>(savedPatient, HttpStatus.CREATED);
	}
	
	/** Handles the request for a specific patient */
	@RequestMapping(method = RequestMethod.GET, value = "/{patientID}")
	ResponseEntity<Patient> readPatient(@PathVariable Long patientID){
		logger.info("FLAG patient/1 called");
		// Initialize the HTTP status to be returned
		HttpStatus responseStatus;
		
		// Call the patient service to look for a patient with given ID in the repository
		Patient returnedPatient = this.patientService.read(patientID);
		
		if(returnedPatient == null){
			responseStatus = HttpStatus.NOT_FOUND;
		}
		else{
			responseStatus = HttpStatus.OK;
		}
		
		// Return the patient and the appropriate HTTP status
		return new ResponseEntity<Patient>(returnedPatient, responseStatus);
	}
	
	/** Handles the request for displaying all of the patients in the repository */
	@RequestMapping(method = RequestMethod.GET)
	ResponseEntity<List<Patient>> readAllPatient(){
		// logger.info("FLAG patient/1 called");
		// Initializes the HTTP status to be returned
		HttpStatus responseStatus;
		
		// Obtain the list of patients in the repository
		List<Patient> returnedPatientList = this.patientService.readAll();
		
		// logger.info("Flag" + returnedPatientList.get(0).getName());
		if(returnedPatientList == null){
			responseStatus = HttpStatus.NOT_FOUND;
			logger.info("FLAG - NOT FOUND");
		}
		else if(returnedPatientList.size() == 0){
			responseStatus = HttpStatus.NOT_FOUND;
			logger.info("FLAG - NOT FOUND");
		}
		else{
			responseStatus = HttpStatus.OK;
		}
		
		// Return the list of patients and the appropriate HTTP status
		return new ResponseEntity<List<Patient>>(returnedPatientList, responseStatus);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{patientId}")
	ResponseEntity<HttpStatus> deletePatient(@PathVariable Long patientId){
	    HttpStatus responseStatus;
	    
	    Patient patientToDelete = this.patientService.read(patientId);
	    if(patientToDelete == null){
	        responseStatus = HttpStatus.NOT_FOUND;
	    }
	    else{
	        this.patientService.delete(patientId);
	        responseStatus = HttpStatus.OK;
	    }
	    logger.info("Flag " + responseStatus);
	    return new ResponseEntity<HttpStatus>(responseStatus);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{patientId}")
	ResponseEntity<Patient> putPatient(@PathVariable Long patientId, @Valid @RequestBody Patient bodyPatient){
	    HttpStatus responseStatus;
	    Patient updatedPatient = null;
	    
	    Patient patientToEdit = this.patientService.read(patientId);
	    if(patientToEdit == null){
	        responseStatus = HttpStatus.NOT_FOUND;
	    }
	    else{
	        updatedPatient = this.patientService.edit(patientToEdit, bodyPatient);
	        responseStatus = HttpStatus.OK;
	    }
	    
	    return new ResponseEntity<Patient>(updatedPatient, responseStatus);
	}
}