package com.example.proyekpamnote;

public class Note {
    private String key, judul, deskripsi, nama;

    public Note() {
    }

    public Note(String key, String title, String description) {
        this.key = key;
        this.judul = judul;
        this.deskripsi = deskripsi;
    }

    public String getTitle() {
        return judul;
    }

    public void setTitle(String title) {
        this.judul = judul;
    }

    public String getDescription() {
        return deskripsi;
    }

    public void setDescription(String description) {
        this.deskripsi = deskripsi;
    }

    public String getName() { return nama; }

    public void setName() { this.nama = nama; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


