package com.mbp.sushruta_v1;

public class GetPatientDetails {

    public String Name,Age,BloodGroup,Gender,PatientID,Address,Insurance_ID,Height,Weight,Aadhar_no,ImageUrl,UserName;


    public void setName(String Name){
       this.Name=Name;
    }

    public void setAge(String Age){
        this.Age=Age;
    }
    public void setImageUrl(String ImageUrl){
        this.ImageUrl=ImageUrl;
    }
    public void setGender(String Gender){
       this.Gender=Gender;
    }

    public void setBloodGroup(String BloodGroup){
       this.BloodGroup=BloodGroup;
    }

    public void setPatientID(String PatientId){
        this.PatientID=PatientId;
    }



    public void setAddress(String Address){
       this.Address=Address;

    }

    public void setInsurance_ID(String Insurance_ID){
        this.Insurance_ID=Insurance_ID;
    }

    public void setHeight(String Height){
       this.Height=Height;
    }

    public void setWeight(String Weight)
    {
        this.Weight=Weight;
    }

    public void setAadhar_no(String aadhar_no) {
       Aadhar_no=aadhar_no;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAadhar_no() {
        return Aadhar_no;
    }

    public String getAddress() {
        return Address;
    }

    public String getAge() {
        return Age;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public String getGender() {
        return Gender;
    }

    public String getHeight() {
        return Height;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getInsurance_ID() {
        return Insurance_ID;
    }

    public String getName() {
        return Name;
    }

    public String getPatientID() {
        return PatientID;
    }

    public String getWeight() {
        return Weight;
    }

    public String getUserName() {
        return UserName;
    }
}
