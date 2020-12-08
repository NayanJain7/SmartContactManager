package com.smartcontactmanager.nayan.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="USER")

@Data
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	@NotBlank(message="name field is required.")
	@Size(min=2,max=20,message="length between 3 to 20 character.")
	private String name;
	@Column(unique= true )
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-.]+com")
	private String email;
	@Size(min=10,max=10,message="must be contain 10 digit only.")
	private String phone;
	@Size(min=8,message="must be greater then 8 character.")
	private String password;
	private String question;
	private String answer;
	private String role;
	private boolean enables;
	private String imageUrl;
	@Column(length = 500)
	private String about;
	
	//work of orphan removal is that it remove the child entity from database whem it unlink with parent entity
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="user",orphanRemoval = true)
	private List<Contact> contacts = new ArrayList<>();
	
	
	

}
