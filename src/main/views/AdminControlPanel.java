package main.views;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import main.models.AnalyticsVisitor;
import main.models.Component;
import main.models.Group;
import main.models.User;

public class AdminControlPanel extends JFrame {
    private static AdminControlPanel instance = null;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private JTextField userIdTextField;
    private JTextField groupIdTextField;
    private DefaultListModel<String> userModel = new DefaultListModel<>();
    private User currentUser;
    private Group currentGroup;

    // Private constructor to prevent instantiation outside this class
    private AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    // Singleton pattern to ensure only one instance of this panel
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    private void initializeComponents() {
        // Setup the layout and the basic components for managing users/groups
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300); // Adjust this value if needed
        splitPane.setLeftComponent(setupTree());
        splitPane.setRightComponent(setupManagementComponents());
        getContentPane().add(splitPane);
    }

    private JScrollPane setupTree() {
        rootNode = new DefaultMutableTreeNode(new Group("root", "Root Group"));
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setupTreeSelectionListener();
        return treeScrollPane;
    }

    public void actionPerformed(ActionEvent e) {
        Object nodeInfo = rootNode.getUserObject();
        if (nodeInfo instanceof Group) {
            Group group = (Group) nodeInfo;
            JOptionPane.showMessageDialog(this, "Group ID: " + group.getId() + "\nGroup Name: " + group.getName(), "Group Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid object type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTreeSelectionListener() {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof User) {
                    currentUser = (User) userObject;
                    currentGroup = (Group) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
                } else if (userObject instanceof Group) {
                    currentUser = null;
                    currentGroup = (Group) userObject;
                } else {
                    currentUser = null;
                    currentGroup = null;
                }
            }
        });
    }

    private JPanel setupManagementComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        userIdTextField = new JTextField(10);
        JButton addUserButton = new JButton("Add User");
        groupIdTextField = new JTextField(10);
        JButton addGroupButton = new JButton("Add Group");
        
        JButton viewUserButton = new JButton("View User");
        JButton validateIDsButton = new JButton("Validate IDs");
        JButton analyticsButton = new JButton("Show Analytics");
        JButton viewGroupDetailsButton = new JButton("View Group Details");
        JButton updateGroupButton = new JButton("Update Group");
        JButton findLastUpdatedUserButton = new JButton("Find Last Updated User");
        JButton removeUserButton = new JButton("Remove User");
        JButton removeGroupButton = new JButton("Remove Group");

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("User ID: "), gbc);
        gbc.gridx = 1;
        controlPanel.add(userIdTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(addUserButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Group ID: "), gbc);
        gbc.gridx = 1;
        controlPanel.add(groupIdTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(addGroupButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        controlPanel.add(viewUserButton, gbc);

        gbc.gridy = 4;
        controlPanel.add(validateIDsButton, gbc);

        gbc.gridy = 5;
        controlPanel.add(analyticsButton, gbc);

        gbc.gridy = 6;
        controlPanel.add(viewGroupDetailsButton, gbc);

        gbc.gridy = 7;
        controlPanel.add(updateGroupButton, gbc);

        gbc.gridy = 8;
        controlPanel.add(findLastUpdatedUserButton, gbc);

        gbc.gridy = 9;
        controlPanel.add(removeUserButton, gbc);

        gbc.gridy = 10;
        controlPanel.add(removeGroupButton, gbc);

        // Listeners for the buttons
        addUserButton.addActionListener(e -> {
            String userId = userIdTextField.getText().trim();
            if (currentGroup != null && !userId.isEmpty() && !groupContainsUser(currentGroup, userId)) {
                addUser(userId, currentGroup);
                userIdTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "User ID already exists or no group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        addGroupButton.addActionListener(e -> {
            String groupId = groupIdTextField.getText().trim();
            if (currentGroup != null && !groupId.isEmpty() && !groupContainsGroup(currentGroup, groupId)) {
                addGroup(groupId, currentGroup);
                groupIdTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Group ID already exists or no parent group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewUserButton.addActionListener(e -> {
            if (currentUser != null) {
                openUserView(currentUser);
            } else {
                JOptionPane.showMessageDialog(this, "No user selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewGroupDetailsButton.addActionListener(e -> {
            if (currentGroup != null) {
                displayGroupDetails(currentGroup);
            } else {
                JOptionPane.showMessageDialog(this, "No group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateGroupButton.addActionListener(e -> { // Add action listener for update group button
            if (currentGroup != null) {
                updateGroupDetails(currentGroup);
            } else {
                JOptionPane.showMessageDialog(this, "No group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeUserButton.addActionListener(e -> {
            if (currentUser != null && currentGroup != null) {
                removeUser(currentUser, currentGroup);
                updateTree();
                currentUser = null;  // Optionally reset the current user
            } else {
                JOptionPane.showMessageDialog(this, "No user or group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeGroupButton.addActionListener(e -> {
            if (currentGroup != null) {
                removeGroup(currentGroup);
                updateTree();
                currentGroup = null;  // Optionally reset the current group
            } else {
                JOptionPane.showMessageDialog(this, "No group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        analyticsButton.addActionListener(e -> performAnalytics());
        validateIDsButton.addActionListener(e -> validateIDs());
        findLastUpdatedUserButton.addActionListener(e -> findLastUpdatedUser());
        return controlPanel;
    }

    private void openUserView(User user) {
        UserView userView = new UserView(user);
        userView.setVisible(true);
    }

    private void addUser(String userId, Group group) {
        if (!groupContainsUser(group, userId)) {
            User newUser = new User(userId);
            group.add(newUser);
            UserView.UserStorage.addUser(newUser); // Add user to storage
            DefaultMutableTreeNode groupNode = findNodeForGroup(group);
            if (groupNode != null) {
                DefaultMutableTreeNode newUserNode = new DefaultMutableTreeNode(newUser);
                groupNode.add(newUserNode);
                treeModel.reload(groupNode); // Refresh the specific part of the tree
                selectAndScrollToNode(newUserNode);
            } else {
                JOptionPane.showMessageDialog(this, "Group node not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayGroupDetails(Group group) {
        String message = "Group ID: " + group.getId() + "\n" +
                        "Group Name: " + group.getName() + "\n" +
                        "Creation Time: " + new Date(group.getCreationTime());
        JOptionPane.showMessageDialog(this, message, "Group Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeUser(User user, Group group) {
        if (group != null && user != null) {
            group.remove(user);
            DefaultMutableTreeNode userNode = findNodeForUser(user);
            if (userNode != null) {
                ((DefaultMutableTreeNode) userNode.getParent()).remove(userNode);
                treeModel.reload(rootNode);
                JOptionPane.showMessageDialog(this, "User removed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "User node not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addGroup(String groupId, Group parentGroup) {
        if (!groupContainsGroup(parentGroup, groupId)) {
            Group newGroup = new Group(groupId, groupId); // Passing groupId as both id and name for simplicity
            parentGroup.add(newGroup);
            DefaultMutableTreeNode parentNode = findNodeForGroup(parentGroup);
            if (parentNode != null) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newGroup);
                parentNode.add(newNode);
                treeModel.reload(parentNode); // Only reload the part of the tree that has changed
                selectAndScrollToNode(newNode);
            } else {
                JOptionPane.showMessageDialog(this, "Parent group not found in the tree.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Group ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeGroup(Group group) {
        if (group != null) {
            Group parentGroup = findParentGroup(rootNode, group);
            if (parentGroup != null) {
                parentGroup.remove(group);
                DefaultMutableTreeNode groupNode = findNodeForGroup(group);
                if (groupNode != null) {
                    ((DefaultMutableTreeNode) groupNode.getParent()).remove(groupNode);
                    treeModel.reload(rootNode);
                    JOptionPane.showMessageDialog(this, "Group removed successfully.");
                }
            }
        }
    }

    private Group findParentGroup(DefaultMutableTreeNode node, Group group) {
        if (node.getUserObject() instanceof Group && ((Group) node.getUserObject()).getChildren().contains(group)) {
            return (Group) node.getUserObject();
        }

        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            TreeNode child = children.nextElement();
            Group result = findParentGroup((DefaultMutableTreeNode) child, group);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void updateGroupDetails(Group group) {
        if (group != null) {
            // Brings up a dialog to edit details like group name
            String newName = JOptionPane.showInputDialog(this, "Enter new group name:", group.getName());
            if (newName != null && !newName.isEmpty()) {
                group.setName(newName);
                updateTree();
                JOptionPane.showMessageDialog(this, "Group details updated successfully.");
            }
        }
    }

    private void performAnalytics() {
        // Assuming `Group` and `User` classes have methods to accept and process a visitor
        Object userObject = rootNode.getUserObject();
        if (userObject instanceof Group) {
            AnalyticsVisitor visitor = new AnalyticsVisitor();
        ((Group) userObject).accept(visitor); // Cast to Group if you're sure about the type
            
            String message = "Total Users: " + visitor.getUserCount() + 
                            "\nTotal Groups: " + visitor.getGroupCount() + 
                            "\nTotal Tweets: " + visitor.getTweetCount() + 
                            "\nPositive Tweets: " + String.format("%.2f%%", visitor.getPositiveTweetPercentage());
            
            JOptionPane.showMessageDialog(this, message, "Analytics Summary", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Root is not a group.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateIDs() {
        Set<String> allIDs = new HashSet<>();
        Set<String> duplicateIDs = new HashSet<>();
        List<String> invalidIDs = new ArrayList<>();

        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            Object userObject = currentNode.getUserObject();
            if (userObject instanceof Component) {
                String id = ((Component) userObject).getId();
                if (!allIDs.add(id)) {
                    duplicateIDs.add(id);
                }
                if (id.contains(" ")) {
                    invalidIDs.add(id);
                }
            }
        }

        if (duplicateIDs.isEmpty() && invalidIDs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All IDs are valid.", "Validation Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "Invalid IDs found:\n";
            if (!duplicateIDs.isEmpty()) {
                message += "Duplicate IDs: " + String.join(", ", duplicateIDs) + "\n";
            }
            if (!invalidIDs.isEmpty()) {
                message += "IDs containing spaces: " + String.join(", ", invalidIDs);
            }
            JOptionPane.showMessageDialog(this, message, "Validation Result", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findLastUpdatedUser() {
        User lastUpdatedUser = null;
        long lastUpdateTime = 0;

        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            Object userObject = currentNode.getUserObject();
            if (userObject instanceof User) {
                User user = (User) userObject;
                if (user.getLastUpdateTime() > lastUpdateTime) {
                    lastUpdateTime = user.getLastUpdateTime();
                    lastUpdatedUser = user;
                }
            }
        }

        if (lastUpdatedUser != null) {
            JOptionPane.showMessageDialog(this, "Last updated user: " + lastUpdatedUser.getId(), "Last Updated User", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No users found.", "Last Updated User", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean groupContainsUser(Group group, String userId) {
        return group.getChildren().stream()
            .anyMatch(component -> component instanceof User && ((User) component).getId().equals(userId));
    }

    private boolean groupContainsGroup(Group group, String groupId) {
        return group.getChildren().stream()
            .anyMatch(component -> component instanceof Group && ((Group) component).getId().equals(groupId));
    }

    private void updateTree() {
        rootNode.removeAllChildren();
        addGroupNodes((Group) rootNode.getUserObject(), rootNode);
        treeModel.reload(rootNode);
    }

    // Method to update the user list in the UI
    public void updateUserList(List<User> users) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear(); // Clear the existing contents
            for (User user : users) {
                userModel.addElement(user.getId()); // Add user ID to the list model
            }
        });
    }

    private void addGroupNodes(Group group, DefaultMutableTreeNode node) {
        for (Component member : group.getChildren()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(member);
            node.add(childNode);
            if (member instanceof Group) {
                addGroupNodes((Group) member, childNode);
            }
        }
    }

    private DefaultMutableTreeNode findNodeForUser(User user) {
        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            if (currentNode.getUserObject() instanceof User && ((User) currentNode.getUserObject()).getId().equals(user.getId())) {
                return currentNode;
            }
        }
        return null; // Return null if no node is found
    }

    private DefaultMutableTreeNode findNodeForGroup(Group group) {
        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            if (currentNode.getUserObject() instanceof Group && ((Group) currentNode.getUserObject()).getId().equals(group.getId())) {
                return currentNode;
            }
        }
        return null; // Return null if no node is found
    }

    private void selectAndScrollToNode(Object identifier) {
        DefaultMutableTreeNode node = null;

        if (identifier instanceof DefaultMutableTreeNode) {
            // If the identifier is already a TreeNode, use it directly.
            node = (DefaultMutableTreeNode) identifier;
        } else if (identifier instanceof String) {
            // If the identifier is a String, search for the node.
            String id = (String) identifier;
            Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
            while (enumeration.hasMoreElements()) {
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
                if (currentNode.getUserObject() instanceof Component && 
                    ((Component) currentNode.getUserObject()).getId().equals(id)) {
                    node = currentNode;
                    break;
                }
            }
        }

        // Scroll to the found node, if any.
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        } else {
            System.err.println("Node not found for identifier: " + identifier);
        }
    }
}