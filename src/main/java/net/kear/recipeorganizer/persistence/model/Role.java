package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ROLE")
public class Role implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String TYPE_GUEST = "GUEST";
	public static final String TYPE_AUTHOR = "AUTHOR";
	public static final String TYPE_EDITOR = "EDITOR";
	public static final String TYPE_ADMIN = "ADMIN";
	public static final String TYPE_DEFAULT = "GUEST";

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_SEQ")
	@SequenceGenerator(name = "ROLE_SEQ", sequenceName = "ROLE_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "NAME")
	@Size(max=10)
	private String name;

	@Column(name = "DESCRIPTION")
	@Size(max=20)
	private String description;
	
    /*@OneToMany(mappedBy = "role")
    private Collection<User> user;*/
	
    //need to rethink how privileges work - this version returns multiple records for each user's role object
    /*@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "ROLES_PRIVILEGES", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), 
    	inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID") )
    private Collection<Privilege> privileges;*/
    
	public Role() {};
	
	public Role(String name) {
		super();
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/*public Collection<User> getUsers() {
        return user;
    }

    public void setUsers(final Collection<User> user) {
        this.user = user;
    }*/

    /*public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(final Collection<Privilege> privileges) {
        this.privileges = privileges;
    }*/

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role role = (Role) obj;
        if (!role.equals(role.name)) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", description=" + description + "]";
	}   
}
