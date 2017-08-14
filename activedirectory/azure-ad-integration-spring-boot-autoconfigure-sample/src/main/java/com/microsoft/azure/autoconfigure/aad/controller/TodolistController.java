/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad.controller;

import com.microsoft.azure.autoconfigure.aad.UserGroup;
import com.microsoft.azure.autoconfigure.aad.UserPrincipal;
import com.microsoft.azure.autoconfigure.aad.model.TodoItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TodolistController {
    private final List<TodoItem> todoList = new ArrayList<TodoItem>();

    public TodolistController() {
        todoList.add(0, new TodoItem(2398, "anything", "whoever"));
    }

    @RequestMapping("/home")
    public Map<String, Object> home() {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "home");
        return model;
    }

    /**
     * HTTP GET
     */
    @RequestMapping(value = "/api/todolist/{index}",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTodoItem(@PathVariable("index") int index) {
        if (index > todoList.size() - 1) {
            return new ResponseEntity<Object>(new TodoItem(-1, "index out of range", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TodoItem>(todoList.get(index), HttpStatus.OK);
    }

    /**
     * HTTP GET ALL
     */
    @RequestMapping(value = "/api/todolist", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TodoItem>> getAllTodoItems() {
        return new ResponseEntity<List<TodoItem>>(todoList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_group1')")
    @RequestMapping(value = "/api/todolist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewTodoItem(@RequestBody TodoItem item) {
        item.setID(todoList.size() + 1);
        todoList.add(todoList.size(), item);
        return new ResponseEntity<String>("OK", HttpStatus.CREATED);
    }

    /**
     * HTTP PUT
     */
    @PreAuthorize("hasRole('ROLE_group1')")
    @RequestMapping(value = "/api/todolist", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTodoItem(@RequestBody TodoItem item) {
        final List<TodoItem> find =
                todoList.stream().filter(i -> i.getID() == item.getID()).collect(Collectors.toList());
        if (!find.isEmpty()) {
            todoList.set(todoList.indexOf(find.get(0)), item);
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<String>("NOT_FOUND", HttpStatus.OK);
    }

    /**
     * HTTP DELETE
     */
    //@PreAuthorize("hasRole('ROLE_group1')")
    @RequestMapping(value = "/api/todolist/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTodoItem(@PathVariable("id") int id) {
        final UserPrincipal current = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (current.isMemberOf(
                new UserGroup("fa7ad436-d32b-464a-928d-43e642711c6c", "group1"))) {
            final List<TodoItem> find = todoList.stream().filter(i -> i.getID() == id).collect(Collectors.toList());
            if (!find.isEmpty()) {
                todoList.remove(todoList.indexOf(find.get(0)));
                return new ResponseEntity<String>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<String>("NOT_FOUND", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("FORBIDDEN", HttpStatus.OK);
        }

    }
}