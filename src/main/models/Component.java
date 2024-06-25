package main.models;

public interface Component {
    void add(Component component);
    void remove(Component component);
    Component getChild(int i);
    String getId();
    void accept(Visitor visitor); // Method to return a unique identifier, useful for displaying in UI
}