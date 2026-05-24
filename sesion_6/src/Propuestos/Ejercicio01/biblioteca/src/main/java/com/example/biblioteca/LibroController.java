package com.example.biblioteca;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/libros")
public class LibroController {
    List<Libro> libros = new ArrayList<>();

    public LibroController() {
        // Libros de prueba
        libros.add(new Libro(1, "Clean Code", "Robert C. Martin", "Tecnología"));
        libros.add(new Libro(2, "The Pragmatic Programmer", "David Thomas", "Tecnología"));
        libros.add(new Libro(3, "Design Patterns", "Gang of Four", "Tecnología"));
        libros.add(new Libro(4, "Refactoring", "Martin Fowler", "Tecnología"));
        libros.add(new Libro(5, "The Lord of the Rings", "J.R.R. Tolkien", "Fantasía"));
        libros.add(new Libro(6, "1984", "George Orwell", "Ficción"));
        libros.add(new Libro(7, "To Kill a Mockingbird", "Harper Lee", "Ficción"));
        libros.add(new Libro(8, "The Great Gatsby", "F. Scott Fitzgerald", "Ficción"));
        libros.add(new Libro(9, "Pride and Prejudice", "Jane Austen", "Romance"));
        libros.add(new Libro(10, "The Catcher in the Rye", "J.D. Salinger", "Ficción"));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listar() {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Libros leídos exitosamente");
        response.put("data", libros);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        Libro libro = libros.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (libro != null) {
            response.put("mensaje", "Libro encontrado");
            response.put("data", libro);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("mensaje", "Libro no encontrado");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> agregar(@RequestBody Libro libro) {
        libros.add(libro);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Libro creado exitosamente");
        response.put("data", libro);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable int id, @RequestBody Libro libroActualizado) {
        Map<String, Object> response = new HashMap<>();
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getId() == id) {
                libros.set(i, libroActualizado);
                response.put("mensaje", "Libro actualizado exitosamente");
                response.put("data", libroActualizado);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        response.put("mensaje", "Libro no encontrado");
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean eliminado = libros.removeIf(l -> l.getId() == id);
        
        if (eliminado) {
            response.put("mensaje", "Libro eliminado exitosamente");
            response.put("id", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("mensaje", "Libro no encontrado");
            response.put("id", id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
