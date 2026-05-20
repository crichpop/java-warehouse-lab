package ru.student.taskmanager;

import ru.student.taskmanager.service.TaskManager;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Многопоточная система обработки заказов ===");
        TaskManager taskManager = new TaskManager();
        taskManager.start();
        System.out.println("=== Работа завершена ===");
    }
}
