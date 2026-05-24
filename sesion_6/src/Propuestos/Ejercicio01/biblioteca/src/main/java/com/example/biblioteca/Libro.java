package com.example.biblioteca;

public class Libro {
    private int id;
    private String nombre;
    private String autor;
    private String genero;

    public Libro(int id, String nombre, String autor, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", autor='" + autor + '\'' +
                ", genero='" + genero + '\'' +
                '}';
    }
}
