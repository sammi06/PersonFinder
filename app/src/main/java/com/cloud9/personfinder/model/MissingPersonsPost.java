package com.cloud9.personfinder.model;

public class MissingPersonsPost {
    String personName, personAge, personAddress,personLostAddress, personCity,personInstructions, personContactone,personContacttwo, imageId ,personCnic, personLocation;

    public MissingPersonsPost(String personName, String personAge, String personAddress,String personLostAddress, String personCity, String personInstructions, String personContactone, String personContacttwo, String imageId, String personCnic, String personLocation) {
        this.personName = personName;
        this.personAge = personAge;
        this.personAddress = personAddress;
        this.personLostAddress = personLostAddress;
        this.personCity = personCity;
        this.personInstructions = personInstructions;
        this.personContactone = personContactone;
        this.personContacttwo = personContacttwo;
        this.imageId = imageId;
        this.personCnic = personCnic;
        this.personLocation = personLocation;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonAge() {
        return personAge;
    }

    public void setPersonAge(String personAge) {
        this.personAge = personAge;
    }

    public String getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(String personAddress) {
        this.personAddress = personAddress;
    }

    public String getPersonLostAddress() {
        return personLostAddress;
    }

    public void setPersonLostAddress(String personLostAddress) {
        this.personLostAddress = personLostAddress;
    }

    public String getPersonCity() {
        return personCity;
    }

    public void setPersonCity(String personCity) {
        this.personCity = personCity;
    }

    public String getPersonInstructions() {
        return personInstructions;
    }

    public void setPersonInstructions(String personInstructions) {
        this.personInstructions = personInstructions;
    }

    public String getPersonContactone() {
        return personContactone;
    }

    public void setPersonContactone(String personContactone) {
        this.personContactone = personContactone;
    }

    public String getPersonContacttwo() {
        return personContacttwo;
    }

    public void setPersonContacttwo(String personContacttwo) {
        this.personContacttwo = personContacttwo;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPersonCnic() {
        return personCnic;
    }

    public void setPersonCnic(String personCnic) {
        this.personCnic = personCnic;
    }

    public String getPersonLocation() {
        return personLocation;
    }

    public void setPersonLocation(String personLocation) {
        this.personLocation = personLocation;
    }
}
