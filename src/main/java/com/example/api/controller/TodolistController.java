package com.example.api.controller;

import com.example.api.model.TodoItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yaweiw on 7/17/2017.
 */
@RestController
@RequestMapping("/api")
public class TodolistController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<TodoItem> Todolist = new ArrayList<TodoItem>();
    public TodolistController() {
        Todolist.add(0, new TodoItem(2398,"anything","whoever"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/protected")
    public Map<String,Object> home() {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    /**
     * HTTP GET
     * */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @RequestMapping(value="/todolist/{index}",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTodoItem(@PathVariable("index") int index) {
        if (index > Todolist.size()) {
            return new ResponseEntity<Object>(new TodoItem(-1,"index out of range", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TodoItem>(Todolist.get(index), HttpStatus.OK);
    }

    /**
     * HTTP GET ALL
     * */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @RequestMapping(value="/todolist",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TodoItem>> getAllTodoItems() {
        return new ResponseEntity<List<TodoItem>>(Todolist, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/todolist",method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewTodoItem(@RequestBody TodoItem item) {
        item.setID(Todolist.size() + 1);
        Todolist.add(Todolist.size(), item);
        logger.info("###addNewTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    /**
     * HTTP PUT
     * */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/todolist",method = RequestMethod.PUT,  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTodoItem(@RequestBody TodoItem item) {
        List<TodoItem> find = Todolist.stream().filter(i -> i.getID() == item.getID()).collect(Collectors.toList());
        if (!find.isEmpty()) {
            Todolist.set(Todolist.indexOf(find.get(0)), item);
            logger.info("###updateTodoItem:Todolist.size = " + Todolist.size());
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        logger.info("###updateTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    /**
     * HTTP DELETE
     * */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/todolist/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTodoItem(@PathVariable("id") int id)
    {
        List<TodoItem> find = Todolist.stream().filter(i -> i.getID() == id).collect(Collectors.toList());
        if (!find.isEmpty()) {
            Todolist.remove(Todolist.indexOf(find.get(0)));
            logger.info("###deleteTodoItem:Todolist.size = " + Todolist.size());
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        logger.info("###deleteTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
}
