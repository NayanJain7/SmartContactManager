package com.smartcontactmanager.nayan.Entity;



import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name="CONTACT")
public class Contact {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		private int cid;
		private String name;
		private String nickName;
		private String email;
		private String phone;
		private String work;
		private String image;
		private String description;
		
		@ManyToOne
		@JsonIgnore
		private User user;
		
		@Override
		public boolean equals(Object obj) {
			
			return this.cid==((Contact)obj).getCid();
	}
}
