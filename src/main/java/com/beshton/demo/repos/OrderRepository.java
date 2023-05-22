package com.beshton.demo.repos;
import com.beshton.demo.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import com.beshton.demo.entities.*;
import com.beshton.demo.exceptions.*;
import com.beshton.demo.advices.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
}