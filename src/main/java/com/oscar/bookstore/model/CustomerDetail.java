package com.oscar.bookstore.model;


import javax.persistence.*;

@Entity
@Table(name = "customer_detail")
public class CustomerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "age")
    private String age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "love_book_type")
    private String loveBookType;

    @Column(name = "hobby")
    private String hobby;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLoveBookType() {
        return loveBookType;
    }

    public void setLoveBookType(String loveBookType) {
        this.loveBookType = loveBookType;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CustomerDetail{" +
                "id=" + id +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", loveBookType='" + loveBookType + '\'' +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
