package com.beshton.demo.entities;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.beshton.demo.entities.*;
import com.beshton.demo.exceptions.*;
import com.beshton.demo.repos.*;
import com.beshton.demo.advices.*;
import com.beshton.demo.controllers.CustomerOrderController;

import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.problem.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

    @Override
    public EntityModel<Order> toModel(Order order) {

        // Unconditional links to single-item resource and aggregate root

        EntityModel<Order> orderModel = EntityModel.of(order,
                linkTo(methodOn(CustomerOrderController.class).one(order.getId())).withSelfRel(),
                linkTo(methodOn(CustomerOrderController.class).all()).withRel("orders"));

        // Conditional links based on state of the order

        if (order.getStatus() == Status.IN_PROGRESS) {
            orderModel.add(linkTo(methodOn(CustomerOrderController.class).cancel(order.getId())).withRel("cancel"));
            orderModel.add(linkTo(methodOn(CustomerOrderController.class).complete(order.getId())).withRel("complete"));
        }

        return orderModel;
    }

}