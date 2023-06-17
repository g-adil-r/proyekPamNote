package com.example.proyekpamnote;

public class Note {
    private String key, judul, deskripsi, nama;

    public Note() {
    }

    public Note(String key, String judul, String deskripsi) {
        this.key = key;
        this.judul = judul;
        this.deskripsi = deskripsi;
    }

    public String getTitle() {
        return judul;
    }

    public void setTitle(String judul) {
        this.judul = judul;
    }

    public String getDescription() {
        return deskripsi;
    }

    public void setDescription(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getName() { return nama; }

    public void setName(String nama) { this.nama = nama; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


