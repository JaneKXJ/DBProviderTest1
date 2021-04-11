package com.example.dbprovidertest1.data;

public class UserInfo {
    private int id;
    private String name;
    private int age;
    private String job;

    public UserInfo(String name, int age, String job) {
        this.name = name;
        this.age = age;
        this.job = job;
    }

    public UserInfo(int id, String name, int age , String job) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                '}';
    }
}