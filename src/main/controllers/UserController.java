package main.controllers;

import main.models.*;
import main.views.*;

public class UserController {
   private AdminControlPanel view;
   private Group model;

   public UserController(AdminControlPanel view, Group model) {
      this.view = view;
      this.model = model;
   }

   public void addUser(String userId) {
      User user = new User(userId);
      model.add(user);  // add() updates the model
      view.updateUserList(model.getUsers());  // Update view
   }
}