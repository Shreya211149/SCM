package com.scm.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class Contact {

	@Id
	private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String adress;
    private String pic;
    
    @Column(length = 5000)
    private String descripstion;
    private boolean favourite;
    private String webLink;
    private String linkedInLink;
    
    private String cloudinaryImagePublicId;
    
   // private List<SocialLink> links=new ArrayList<>();
    
    @ManyToOne
    @JsonIgnore
    private User user;
    @OneToMany(mappedBy = "contact",cascade=CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
	private List<SocialLink> links=new ArrayList<SocialLink>();
    
    
}
