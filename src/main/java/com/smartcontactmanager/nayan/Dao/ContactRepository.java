package com.smartcontactmanager.nayan.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.nayan.Entity.Contact;
import com.smartcontactmanager.nayan.Entity.User;

//make a contact repository so we can also use pagination functionality
public interface ContactRepository extends JpaRepository<Contact, Integer> {

		@Query("from Contact as c where c.user.id = :userid")
		public Page<Contact> findContactByUSer(@Param("userid") int id, Pageable pageable);
		
		public List<Contact> findByNameContainingAndUser(String name, User user);
		
		
}
