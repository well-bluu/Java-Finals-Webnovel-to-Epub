package model;


// Represents a unit of work for the producer-consumer pipeline.
// Contains the chapter URL and its order index.

public class Task {
    public static final int POISON = -1;

    private final int id;
    private final String url;

    public Task(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() { 
        return id; 
    }
    
    public String getUrl() { 
        return url; 
    }
}