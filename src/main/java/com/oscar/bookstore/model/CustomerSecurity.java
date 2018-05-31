package com.oscar.bookstore.model;

import javax.persistence.*;

@Entity
@Table(name = "customer_security")
public class CustomerSecurity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "password")
    private String password;

    @Column(name = "hint")
    private String hint;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public String toString() {
        return "CustomerSecurity{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
