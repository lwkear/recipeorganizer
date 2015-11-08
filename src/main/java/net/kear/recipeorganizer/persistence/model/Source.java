package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "SOURCE")
public class Source implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOURCE_SEQ")
	@SequenceGenerator(name = "SOURCE_SEQ", sequenceName = "SOURCE_SEQ", allocationSize = 1)
	private long id;
	
	@Column(name = "TYPE", nullable = false)
	private String type;

	@Column(name = "COOKBOOK")
	@Size(max=250)	//250
	private String cookbook;

	@Column(name = "COOKBOOK_PAGE")
	private Integer cookbookPage;

	@Column(name = "MAGAZINE")
	@Size(max=250)	//250
	private String magazine;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="MM/dd/yyyy")
	@Column(name = "MAGAZINE_PUBDATE")
	@Past
	private Date magazinePubdate;
	
	@Column(name = "NEWSPAPER")
	@Size(max=250)	//250
	private String newspaper;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="MM/dd/yyyy")
	@Column(name = "NEWSPAPER_PUBDATE")
	@Past
	private Date newspaperPubdate;

	@Column(name = "PERSON")
	@Size(max=250)	//250
	private String person;
	
	@Column(name = "WEBSITE_URL")
	@URL
	private String websiteUrl;
	
	@Column(name = "RECIPE_URL")
	@URL
	private String recipeUrl;

	@Column(name = "OTHER")
	@Size(max=500)	//250
	private String other;

/*    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID", nullable = false)
    private Recipe recipe;*/

    @OneToOne(mappedBy = "recipe")
  	private Recipe recipe;
    
	public Source() {}
	
	public Source(String type, String cookbook, Integer cookbookPage, String magazine, Date magazinePubdate, String newspaper, Date newspaperPubdate, String person,
			String websiteUrl, String recipeUrl, String other) {
		super();
		this.type = type;
		this.cookbook = cookbook;
		this.cookbookPage = cookbookPage;
		this.magazine = magazine;
		this.magazinePubdate = magazinePubdate;
		this.newspaper = newspaper;
		this.newspaperPubdate = newspaperPubdate;
		this.person = person;
		this.websiteUrl = websiteUrl;
		this.recipeUrl = recipeUrl;
		this.other = other;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCookbook() {
		return cookbook;
	}

	public void setCookbook(String cookbook) {
		this.cookbook = cookbook;
	}

	public Integer getCookbookPage() {
		return cookbookPage;
	}

	public void setCookbookPage(Integer cookbookPage) {
		this.cookbookPage = cookbookPage;
	}

	public String getMagazine() {
		return magazine;
	}

	public void setMagazine(String magazine) {
		this.magazine = magazine;
	}

	public Date getMagazinePubdate() {
		return magazinePubdate;
	}

	public void setMagazinePubdate(Date magazinePubdate) {
		this.magazinePubdate = magazinePubdate;
	}

	public String getNewspaper() {
		return newspaper;
	}

	public void setNewspaper(String newspaper) {
		this.newspaper = newspaper;
	}

	public Date getNewspaperPubdate() {
		return newspaperPubdate;
	}

	public void setNewspaperPubdate(Date newspaperPubdate) {
		this.newspaperPubdate = newspaperPubdate;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getRecipeUrl() {
		return recipeUrl;
	}

	public void setRecipeUrl(String recipeUrl) {
		this.recipeUrl = recipeUrl;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public String toString() {
		return "Source [id=" + id 
				+ ", type=" + type 
				+ ", cookbook=" + cookbook 
				+ ", cookbookPage=" + cookbookPage 
				+ ", magazine=" + magazine
				+ ", magazinePubdate=" + magazinePubdate 
				+ ", newspaper=" + newspaper 
				+ ", newspaperPubdate=" + newspaperPubdate 
				+ ", person=" + person 
				+ ", websiteUrl=" + websiteUrl 
				+ ", recipeUrl=" + recipeUrl 
				+ ", other=" + other + "]";
	}
}