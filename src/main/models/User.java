package main.models;

import java.util.ArrayList;
import java.util.List;

import main.views.UserView;

public class User implements Component, Observer, Subject {
   private String id; // Unique identifier for the user
   private List<String> tweets = new ArrayList<>();
   private List<User> followers = new ArrayList<>();
   private List<String> followings = new ArrayList<>(); // List to store the IDs of users this user is following
   private List<Observer> observerList = new ArrayList<>();
   private List<String> newsFeed = new ArrayList<>();
   private long creationTime;
   private long lastUpdateTime;

   public User(String id) {
      this.id = id;
      this.creationTime = System.currentTimeMillis();
      this.lastUpdateTime = this.creationTime;
   }

   public String getId() {
      return this.id;
   }

   public List<String> getFollowings() {
      return new ArrayList<>(followings); // Return a copy of the followings list
   }

   public List<String> getNewsFeed() {
      return new ArrayList<>(newsFeed); // Return a copy of the news feed
   }

   public void postTweet(String tweet) {
      tweets.add(tweet);
      notifyFollowers(tweet); // Notify followers about the tweet
      notifyObservers(tweet); // Notify views or other observers
      lastUpdateTime = System.currentTimeMillis();
   }

   private void notifyFollowers(String tweet) {
      for (User follower : followers) {
         follower.receiveTweet(this.id, tweet);
      }
   }

   public void addObserver(UserView observer) {
      if (!observerList.contains(observer)) {
         observerList.add(observer);
      }
   }

   public long getCreationTime() {
      return creationTime;
   }

   public long getLastUpdateTime() {
      return lastUpdateTime;
   }

   public void setLastUpdateTime(long lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
   }

   // Subject interface methods
   @Override
   public void attach(Observer o) {
      if (!observerList.contains(o)) observerList.add(o);
   }

   @Override
   public void detach(Observer o) {
      observerList.remove(o);
   }

   public void addFollower(User follower) {
      if (!followers.contains(follower)) {
         followers.add(follower);
         followings.add(this.id); // Add this user to the follower's followings list
      }
   }

   public void receiveTweet(String userId, String tweet) {
      newsFeed.add("Tweet from " + userId + ": " + tweet);
   }

   public void notifyObservers(String tweet) {
      for (Observer observer : observerList) {
         observer.update("Tweet from " + id + ": " + tweet);
      }
   }

   // Observer interface method
   @Override
   public void update(String message) {
      newsFeed.add(message);
   }

   public void accept(Visitor visitor) {
      visitor.visit(this);  // Calls visit method for a User object
   }

   @Override
   public String toString() {
      return "User ID: " + id; // Customize as needed for display in GUI or logs
   }

   // Component interface methods
   @Override
   public void add(Component component) {
      throw new UnsupportedOperationException("Not supported operation");
   }

   @Override
   public void remove(Component component) {
      throw new UnsupportedOperationException("Not supported operation");
   }

   @Override
   public Component getChild(int i) {
      throw new UnsupportedOperationException("Not supported operation");
   }
}
