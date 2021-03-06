package xyz.dolphcode.tasktitans.database.tasks;

import java.util.ArrayList;
import java.util.Arrays;

// Represents a group member of a task group
public class GroupMember {

    private ArrayList<String> taskIDs;
    private String memberID;

    // Protected constructors because only classes in this package need to instantiate these
    protected GroupMember(String memberID, ArrayList<String> taskIDs) {
        this.taskIDs = taskIDs;
        this.memberID = memberID;
    }

    protected GroupMember(String memberID, String taskIDs) {
        this.taskIDs = new ArrayList<String>();
        for (String str:taskIDs.split(",")) {
            this.taskIDs.add(str);
        }
        this.memberID = memberID;
    }

    public String getMemberID() { return memberID; }
    public ArrayList<String> getTaskIDs() { return taskIDs; }
}
