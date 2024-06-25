package main.models;

import java.util.ArrayList;
import java.util.List;

public class Group implements Component {
   private String id;
   private String name; // Store the group name
   private List<Component> children = new ArrayList<>();

   public Group(String id, String name) {
      this.id = id;
      this.name = name;
   }

   @Override
   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void add(Component component) {
      children.add(component);
   }

   @Override
   public void remove(Component component) {
      children.remove(component);
   }

   @Override
   public Component getChild(int i) {
      return children.get(i);
   }

   // Method to retrieve all user instances from this group
    public List<User> getUsers() {
      List<User> users = new ArrayList<>();
      for (Component member : children) {
         if (member instanceof User) {
               users.add((User) member);
         } else if (member instanceof Group) {
               users.addAll(((Group) member).getUsers()); // Recursively get users from subgroups
         }
      }
      return users;
   }

   @Override
   public void accept(Visitor visitor) {
      visitor.visit(this);  // Allow the visitor to perform operations specific to Group
      for (Component component : children) {
         component.accept(visitor);  // Recursively accept visitors for all children
      }
   }

   // Method to return all children, useful for UI operations
   public List<Component> getChildren() {
      return new ArrayList<>(children);
   }

   @Override
   public String toString() {
      return "Group: " + name; // Display the group name in the tree
   }
}
