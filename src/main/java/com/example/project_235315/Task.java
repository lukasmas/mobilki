package com.example.project_235315;

import java.util.Date;

public class Task{
    String taskDesc;
    Long taskId;
    Date dueDate;
    Boolean isDone;

    Task(Long id, String desc, Boolean isDone, Date dueDate){
        this.taskId = id;
        this.taskDesc = desc;
        this.isDone = isDone;
        this.dueDate = dueDate;
    }

}
