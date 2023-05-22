package com.beshton.demo.controllers;

import java.util.List;
import java.util.stream.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.problem.*;
import com.beshton.demo.entities.*;
import com.beshton.demo.repos.*;
import com.beshton.demo.exceptions.*;
import com.beshton.demo.advices.*;

@RestController
public class CustomerOrderController {

  private final OrderRepository orderRepository;
  private final OrderModelAssembler assembler;

  public CustomerOrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {

    this.orderRepository = orderRepository;
    this.assembler = assembler;
  }

  @GetMapping("/orders")
  public CollectionModel<EntityModel<Order>> all() {

    List<EntityModel<Order>> orders = orderRepository.findAll().stream() //
        .map(assembler::toModel) //
        .collect(Collectors.toList());

    return CollectionModel.of(orders, //
        linkTo(methodOn(CustomerOrderController.class).all()).withSelfRel());
  }

  @GetMapping("/orders/{id}")
  public EntityModel<Order> one(@PathVariable Long id) {

    Order order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    return assembler.toModel(order);
  }

  @PostMapping("/orders")
  ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

    order.setStatus(Status.IN_PROGRESS);
    Order newOrder = orderRepository.save(order);

    return ResponseEntity //
        .created(linkTo(methodOn(CustomerOrderController.class).one(newOrder.getId())).toUri()) //
        .body(assembler.toModel(newOrder));
  }

  @DeleteMapping("/orders/{id}/cancel")
  public ResponseEntity<?> cancel(@PathVariable Long id) {

    Order order = orderRepository.findById(id) //
            .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.CANCELLED);
      return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity //
            .status(HttpStatus.METHOD_NOT_ALLOWED) //
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
            .body(Problem.create() //
                    .withTitle("Method not allowed") //
                    .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
  }

  @PutMapping("/orders/{id}/complete")
  public ResponseEntity<?> complete(@PathVariable Long id) {

    Order order = orderRepository.findById(id) //
            .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.COMPLETED);
      return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity //
            .status(HttpStatus.METHOD_NOT_ALLOWED) //
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
            .body(Problem.create() //
                    .withTitle("Method not allowed") //
                    .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
  }
}