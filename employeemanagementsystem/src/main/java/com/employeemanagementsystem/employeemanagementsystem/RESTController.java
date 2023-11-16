package com.employeemanagementsystem.employeemanagementsystem;

import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpSession;

@RestController
public class RESTController {

    static User admin = new User("admin", "please", "hi", "clean the fridge", "none");
    static User userNotFound = new User(null, null, null, null, "none");

    ArrayList<Integer> taskDataList = new ArrayList<>();
       
    
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Task> currentTasks = new ArrayList<>();

    public static ArrayList<User> getUsers(){
        return users;
    }

    public static ArrayList<User> getTasks(){
        return users;
    }

    public RESTController() {
        users.add(admin);
        users.add(userNotFound);
        TaskData.taskDataLists.put("Sorting", taskDataList);
        taskDataList.add(34);
        taskDataList.add(25);
        taskDataList.add(56);
        taskDataList.add(87);
        taskDataList.add(65);
    }
    

    @PostMapping(value = "/login")
    public String getUser(@RequestBody User SubmittedUsernameAndPassword, HttpSession session) {
        
        User userToLogin = Find.findUser(SubmittedUsernameAndPassword);
        if (userToLogin != userNotFound) {
            String newSessionID = session.getId();
            userToLogin.setSessionID(newSessionID); //does updating the userToLogin update that object in the arrayList? (i think so)
            return newSessionID;
        }
        System.out.println("Could not find user");
        return "FAILED";
    }

    @PostMapping("/session")
    public User verifySessionID(@RequestBody String sessionID) {
        return Find.findSessionID(sessionID);
    }

    @PostMapping("/newuser")
    public Boolean createUser(@RequestBody User SubmittedUser) {
        User foundUser = Find.findUser(SubmittedUser);
        if (foundUser == userNotFound) {
            User createdUser = new User(
                SubmittedUser.getUsername(), 
                SubmittedUser.getPassword(), 
                SubmittedUser.getTeam(),
                null, 
                "none"
            );
            System.out.println(createdUser);
            System.out.println(createdUser.getUsername());
            System.out.println(createdUser.getTeam());
            users.add(createdUser);
            if (!TeamData.getTeams().contains(createdUser.getTeam())) {
                TeamData.addTeam(createdUser.getTeam());
            }
            return true;
        } else {
            System.out.println("User alr exists");
            return false;
        }
    }

    @PostMapping("/signout")
    public void signOut(@RequestBody String sessionID) {
        User userToSignOut = Find.findSessionID(sessionID);
        userToSignOut.setSessionID("none");
    }

    @DeleteMapping(value = "/users/{userID}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userID) {
        return null;
        
    }

    @PostMapping("/createTask")
    public String createTask(@RequestBody Task taskToCreate) { //maybe dropdown menu to create tasks
        String originOfRequestSessionID = taskToCreate.originOfRequestSessionID();
        double completionTimeAvg = 0;
        System.out.println("session ID" + originOfRequestSessionID);
        User originUser = Find.findSessionID(originOfRequestSessionID);
        if (!TaskData.taskDataLists.containsKey(taskToCreate.taskName())) {
            TaskData.createTaskDataArray(taskToCreate.taskName());
        } else {
            completionTimeAvg = Statistics.meanValue(TaskData.taskDataLists.get(taskToCreate.taskName()));
        }
        Task newTask = new Task(taskToCreate.taskName(), taskToCreate.taskDescription(), null,  completionTimeAvg , originUser.getTeam(), originUser.getUsername());
        System.out.println("task that was created: " + newTask);
        currentTasks.add(newTask);
        return "hi";
    }
}