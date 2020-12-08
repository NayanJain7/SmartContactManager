package com.smartcontactmanager.nayan.Controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
	
	@ExceptionHandler(value=Exception.class)
	public String exceptionHandler() {
		return"error";
	}

}
