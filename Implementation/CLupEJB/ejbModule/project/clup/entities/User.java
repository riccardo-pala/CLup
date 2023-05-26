package project.clup.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * Persistent class for users table
 * 
 * @author PalaImmordinoPolvanesi
 *
 */
@Entity
@Table(name = "users")
@NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = ?1 and r.password = ?2")
public class User {
	
	@Id 
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "firstname")
	private String firstname;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	// uni-directional
	@ManyToOne
	@JoinColumn(name = "supermarket_fk")
	private Supermarket favouriteSupermarket;
	
	// bi-directional, the owner stands to the other side...
	// CascadeType here!!! reservation has no meaning without the parent entity of relationship (User)
	@OneToOne(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
	private Reservation reservation;
	
	
	/** 
	 * 
	 * Constructors, getters and setters
	 * 
	 */
	
	public User() {
	}
	
	public User(int id, String username, String password, String firstname, String lastname, Supermarket favouriteSupermarket) {
		this.id = id; //
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.favouriteSupermarket = favouriteSupermarket;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Supermarket getFavouriteSupermarket() {
		return favouriteSupermarket;
	}

	public void setFavouriteSupermarket(Supermarket favouriteSupermarket) {
		this.favouriteSupermarket = favouriteSupermarket;
	}
	
}