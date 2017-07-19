package com.example.api.controller;

import com.example.api.model.TodoItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    }

    /**
     * HTTP GET
     * */
    @RequestMapping(value="/TodoList/{index}",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTodoItem(@PathVariable("index") int index) {
        if (index > Todolist.size()) {
            return new ResponseEntity<Object>(new TodoItem(-1,"index out of range", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TodoItem>(Todolist.get(index), HttpStatus.OK);
    }

    /**
     * HTTP GET ALL
     * */
    @RequestMapping(value="/TodoList",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TodoItem>> getAllTodoItems() {
        return new ResponseEntity<List<TodoItem>>(Todolist, HttpStatus.OK);
    }

    @RequestMapping(value="/TodoList",method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewTodoItem(@RequestBody TodoItem item) {
        item.setID(Todolist.size() + 1);
        Todolist.add(Todolist.size(), item);
        logger.info("addNewTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    /**
     * HTTP PUT
     * */
    @RequestMapping(value="/TodoList",method = RequestMethod.PUT,  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTodoItem(@RequestBody TodoItem item) {
        List<TodoItem> find = Todolist.stream().filter(i -> i.getID() == item.getID()).collect(Collectors.toList());
        if (!find.isEmpty()) {
            Todolist.set(Todolist.indexOf(find.get(0)), item);
            logger.info("updateTodoItem:Todolist.size = " + Todolist.size());
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        logger.info("updateTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    /**
     * HTTP DELETE
     * */
    @RequestMapping(value = "/TodoList/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTodoItem(@PathVariable("id") int id)
    {
        List<TodoItem> find = Todolist.stream().filter(i -> i.getID() == id).collect(Collectors.toList());
        if (!find.isEmpty()) {
            Todolist.remove(Todolist.indexOf(find.get(0)));
            logger.info("deleteTodoItem:Todolist.size = " + Todolist.size());
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        logger.info("deleteTodoItem:Todolist.size = " + Todolist.size());
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
}
