package com.netiapps.planetwork.model;

public class PieChartDAO {

    private String date;

    private String earnings;

    private String leave_balance;

    private String user_id;

    private String t_allowance;

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getEarnings ()
    {
        return earnings;
    }

    public void setEarnings (String earnings)
    {
        this.earnings = earnings;
    }

    public String getLeave_balance ()
    {
        return leave_balance;
    }

    public void setLeave_balance (String leave_balance)
    {
        this.leave_balance = leave_balance;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getT_allowance ()
    {
        return t_allowance;
    }

    public void setT_allowance (String t_allowance)
    {
        this.t_allowance = t_allowance;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [date = "+date+", earnings = "+earnings+", leave_balance = "+leave_balance+", user_id = "+user_id+", t_allowance = "+t_allowance+"]";
    }

}
