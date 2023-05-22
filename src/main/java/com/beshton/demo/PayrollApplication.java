package com.beshton.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.beshton.demo.entities.*;
import com.beshton.demo.repos.*;
import com.beshton.demo.advices.*;
import com.beshton.demo.controllers.*;

@SpringBootApplication
public class PayrollApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayrollApplication.class, args);
	}

}
