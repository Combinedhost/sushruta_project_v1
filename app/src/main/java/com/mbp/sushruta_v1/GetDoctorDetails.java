package com.mbp.sushruta_v1;

public class GetDoctorDetails {
    public String Name,Age,Qualification,ImageUrl,Gender,Designation,Username,DoctorID,Specialization;

    GetDoctorDetails(){

    }
    public void setUsername(String username){
        Username=username;
    }

    public void setName(String Name1){
        Name=Name1;
    }

    public void setAge(String Age1){
        Age=Age1;
    }

    public void setQualification( String Qualification1){
        Qualification=Qualification1;
    }
    public void setImageUrl(String ImageUrl1){
        ImageUrl=ImageUrl1;
    }
    public void setGender(String Gender1){
        Gender=Gender1;
    }
    public void setDesignation(String Designation1){
        Designation=Designation1;
    }
    public void setDoctorID(String Doctorid){
        DoctorID=Doctorid;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }

    public String getName(){
        return Name;
    }

    public String getAge(){
        return Age;
    }

    public String getQualification(){
        return Qualification;
    }

    public String getImageUrl(){
        return ImageUrl;
    }

    public String getGender(){
        return Gender;
    }

    public String getDesignation(){
        return Designation;
    }

    public String getDoctorID() {
        return DoctorID;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public String getUsername() {
        return Username;
    }
}
