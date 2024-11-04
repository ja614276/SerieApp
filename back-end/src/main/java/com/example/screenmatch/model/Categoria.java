package com.example.screenmatch.model;

public enum Categoria {
    Accion("Action","Accion"),
    Romance("Romance","Romance"),
    Comedia("Comedy","Comedia"),
    Drama("Drama","Drama"),
    Crimen("Crime","Crimen"),
    Fantasy("Fantasy","Fantasia"),
    Animation("Animation","Animacion");

    private String categoriaOmdb;
    private String categoriaSpanishOmdb;


    Categoria(String categoriaOmdb, String categoriaSpanishOmdb) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaSpanishOmdb = categoriaSpanishOmdb;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    public static Categoria fromSpanish(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaSpanishOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
