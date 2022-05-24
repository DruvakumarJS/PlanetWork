package com.netiapps.planetwork.mvvm;

public class Jobs {
    private String address;

    private String job_date;

    private String city;

    private String job_id;

    private String latitude;

    private String sr_no;

    private String job_assign_id;

    private String customer_name;

    private String branch;

    private String no_of_visit;

    private String longitude;

    private String status;

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getJob_date ()
    {
        return job_date;
    }

    public void setJob_date (String job_date)
    {
        this.job_date = job_date;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getJob_id ()
    {
        return job_id;
    }

    public void setJob_id (String job_id)
    {
        this.job_id = job_id;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getSr_no ()
    {
        return sr_no;
    }

    public void setSr_no (String sr_no)
    {
        this.sr_no = sr_no;
    }

    public String getJob_assign_id ()
    {
        return job_assign_id;
    }

    public void setJob_assign_id (String job_assign_id)
    {
        this.job_assign_id = job_assign_id;
    }

    public String getCustomer_name ()
    {
        return customer_name;
    }

    public void setCustomer_name (String customer_name)
    {
        this.customer_name = customer_name;
    }

    public String getBranch ()
    {
        return branch;
    }

    public void setBranch (String branch)
    {
        this.branch = branch;
    }

    public String getNo_of_visit ()
    {
        return no_of_visit;
    }

    public void setNo_of_visit (String no_of_visit)
    {
        this.no_of_visit = no_of_visit;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
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
        return "ClassPojo [address = "+address+", job_date = "+job_date+", city = "+city+", job_id = "+job_id+", latitude = "+latitude+", sr_no = "+sr_no+", job_assign_id = "+job_assign_id+", customer_name = "+customer_name+", branch = "+branch+", no_of_visit = "+no_of_visit+", longitude = "+longitude+", status = "+status+"]";
    }
}
