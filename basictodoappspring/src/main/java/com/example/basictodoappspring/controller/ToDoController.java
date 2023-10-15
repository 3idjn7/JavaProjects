package com.example.basictodoappspring.controller;

import com.example.basictodoappspring.model.ToDo;
import com.example.basictodoappspring.repository.ToDoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private static final Logger logger = LoggerFactory.getLogger(ToDoController.class);

    @Autowired
    private ToDoRepository toDoRepository;

    @GetMapping
    public List<ToDo> getAllToDos() {
        logger.info("Received GET request for all ToDos");
        return toDoRepository.findAll();
    }

    @PostMapping
    public ToDo createToDo(@RequestBody ToDo todo) {
        logger.info("Creating ToDo: {}", todo);
        ToDo saveToDo = toDoRepository.save(todo);
        logger.info("Saved ToDo: {}", saveToDo);
        return saveToDo;
    }

    @PutMapping("/{id}")
    public ToDo updateToDo(@PathVariable Long id, @RequestBody ToDo todo) {
        logger.info("Received PUT request to update ToDo with id {}: {}", id, todo);
        Optional<ToDo> optionalToDo = toDoRepository.findById(id);
        if (optionalToDo.isPresent()) {
            ToDo currentToDo = optionalToDo.get();
            currentToDo.setTitle(todo.getTitle());
            currentToDo.setCompleted(todo.isCompleted());
            ToDo updatedToDo = toDoRepository.save(currentToDo);
            logger.info("Updated ToDo: {}", updatedToDo);
            return updatedToDo;
        } else {
            logger.warn("ToDo with id {} not found. Unable to update.", id);
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteToDo(@PathVariable Long id) {
        logger.info("Received DELETE request for ToDo with id {}", id);
        try {
            toDoRepository.deleteById(id);
            logger.info("Deleted ToDo with id {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete ToDo with id {}: {}", id, e.getMessage());
        }
    }
}