package main.models;

public interface Visitor {
   void visit(User user);
   void visit(Group group);
}