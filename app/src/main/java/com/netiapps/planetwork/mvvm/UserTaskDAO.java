package com.netiapps.planetwork.mvvm;

public class UserTaskDAO {
    private Jobs[] jobs;

    private String status;

    public Jobs[] getJobs ()
    {
        return jobs;
    }

    public void setJobs (Jobs[] jobs)
    {
        this.jobs = jobs;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [jobs = "+jobs+", status = "+status+"]";
    }
}
