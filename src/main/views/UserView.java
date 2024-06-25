package main.views;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import main.models.User;
import main.models.Observer;

public class UserView extends JFrame implements Observer {
   private User currentUser;
   private DefaultListModel<String> followingListModel = new DefaultListModel<>();
   private DefaultListModel<String> newsFeedListModel = new DefaultListModel<>();
   private JList<String> followingList = new JList<>(followingListModel);
   private JList<String> newsFeedList = new JList<>(newsFeedListModel);
   private JTextArea userIdTextArea = new JTextArea(1, 20);
   private JButton followUserButton = new JButton("Follow User");
   private JTextArea tweetTextArea = new JTextArea(3, 20);
   private JButton postTweetButton = new JButton("Post Tweet");

   public UserView(User user) {
      this.currentUser = user;
      setTitle("User View - " + user.getId());
      setSize(400, 500);
      setLayout(new BorderLayout());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      currentUser.attach(this); // Ensure this method exists and works as intended in User
      initializeComponents();
   }

   private void initializeComponents() {
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

      // Panel for following users
      JPanel followPanel = new JPanel(new BorderLayout());
      followPanel.setBorder(BorderFactory.createTitledBorder("User ID to Follow"));
      followPanel.add(userIdTextArea, BorderLayout.CENTER);
      followPanel.add(followUserButton, BorderLayout.EAST);
      followUserButton.addActionListener(e -> followUser());

      // Panel for following list
      JPanel followingPanel = new JPanel(new BorderLayout());
      followingPanel.setBorder(BorderFactory.createTitledBorder("Following"));
      followingPanel.add(new JScrollPane(followingList), BorderLayout.CENTER);

      // Panel for tweet posting
      JPanel tweetPanel = new JPanel(new BorderLayout());
      tweetPanel.setBorder(BorderFactory.createTitledBorder("Tweet Message"));
      tweetPanel.add(new JScrollPane(tweetTextArea), BorderLayout.CENTER);
      postTweetButton.addActionListener(e -> postTweet());
      tweetPanel.add(postTweetButton, BorderLayout.SOUTH);

      // Panel for news feed
      JPanel newsFeedPanel = new JPanel(new BorderLayout());
      newsFeedPanel.setBorder(BorderFactory.createTitledBorder("News Feed"));
      newsFeedPanel.add(new JScrollPane(newsFeedList), BorderLayout.CENTER);

      mainPanel.add(followPanel);
      mainPanel.add(followingPanel);
      mainPanel.add(tweetPanel);
      mainPanel.add(newsFeedPanel);

      add(mainPanel, BorderLayout.CENTER);
      updateFollowingList(); // Initial update of the following list
   }

   private void followUser() {
      String userIdToFollow = userIdTextArea.getText().trim();
      if (!userIdToFollow.isEmpty()) {
         User userToFollow = UserStorage.getUserById(userIdToFollow);
         if (userToFollow != null) {
            currentUser.addFollower(userToFollow);
            userToFollow.attach(this); // Attach this view to the followed user to receive updates
            updateFollowingList();
         } else {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   private void postTweet() {
      String tweet = tweetTextArea.getText().trim();
      if (!tweet.isEmpty()) {
         currentUser.postTweet(tweet);
         tweetTextArea.setText(""); // Clear the text area after posting
      }
   }

   public void updateFollowingList() {
      followingListModel.clear();
      for (String userId : currentUser.getFollowings()) {
         followingListModel.addElement(userId);
      }
   }

   @Override
   // This method should update asynchronously on the UI thread
   public void update(String message) {
      SwingUtilities.invokeLater(() -> {
         newsFeedListModel.addElement(message);
      });
   }

   // UserStorage class to simulate user retrieval
   static class UserStorage {
      private static Map<String, User> userMap = new HashMap<>();

      public static void addUser(User user) {
         userMap.put(user.getId(), user);
      }

      public static User getUserById(String userId) {
         return userMap.get(userId);
      }
   }
}
