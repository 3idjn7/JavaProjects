package com.example.basictodoappspring.controller;

import com.example.basictodoappspring.model.ToDo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final List<ToDo> toDos = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @GetMapping
    public List<ToDo> getAllToDos() {
        return toDos;
    }

    @PostMapping
    public ToDo createToDo(@RequestBody ToDo todo) {
        ToDo newToDo = new ToDo();
        newToDo.setId(counter.incrementAndGet());
        newToDo.setTitle(todo.getTitle());
        newToDo.setCompleted(todo.isCompleted());
        toDos.add(newToDo);
        return newToDo;
    }

    @PutMapping("/{id}")
    public ToDo updateToDo(@PathVariable Long id, @RequestBody ToDo todo) {
        for (ToDo t : toDos) {
            if (t.getId().equals(id)) {
                t.setTitle(todo.getTitle());
                t.setCompleted(todo.isCompleted());
                return t;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteToDo(@PathVariable Long id) {
        toDos.removeIf(t -> t.getId().equals(id));
    }
}
