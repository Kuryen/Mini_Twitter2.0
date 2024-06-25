package main.models;

import java.util.HashSet;
import java.util.Set;

public class AnalyticsVisitor implements Visitor {
   private int userCount = 0;
   private int groupCount = 0;
   private int tweetCount = 0;
   private int positiveTweetCount = 0;
   private Set<String> uniqueUsers = new HashSet<>();
   private Set<String> uniqueGroups = new HashSet<>();

   private static final Set<String> positiveWords = Set.of("good", "great", "excellent", "fantastic", "awesome");

   @Override
   public void visit(User user) {
      if (uniqueUsers.add(user.getId())) { // Ensure the user is counted only once
         userCount++;
         tweetCount += user.getNewsFeed().size();
         for (String tweet : user.getNewsFeed()) {
               for (String word : positiveWords) {
                  if (tweet.toLowerCase().contains(word)) {
                     positiveTweetCount++;
                     break;
                  }
               }
         }
      }
   }

   @Override
   public void visit(Group group) {
      if (uniqueGroups.add(group.getId())) { // Ensure the group is counted only once
         groupCount++;
         for (Component component : group.getChildren()) {
               component.accept(this);
         }
      }
   }

   public int getUserCount() {
      return userCount;
   }

   public int getGroupCount() {
      return groupCount;
   }

   public int getTweetCount() {
      return tweetCount;
   }

   public double getPositiveTweetPercentage() {
      return tweetCount == 0 ? 0 : (double) positiveTweetCount / tweetCount * 100;
   }
}