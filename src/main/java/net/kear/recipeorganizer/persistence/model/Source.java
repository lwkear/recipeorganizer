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
import javax.validation.GroupSequence;
import javax.validation.constraints.*;

import net.kear.recipeorganizer.persistence.model.Recipe;

import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "SOURCE")
public class Source implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface SizeGroup {}
	public interface OtherGroup {}
	
	@GroupSequence({SizeGroup.class,OtherGroup.class})
	public interface SourceGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOURCE_SEQ")
	@SequenceGenerator(name = "SOURCE_SEQ", sequenceName = "SOURCE_SEQ", allocationSize = 1)
	private long id;
	
	@Column(name = "TYPE", nullable = false)
	private String type;

	@Column(name = "COOKBOOK")
	@Size(max=250, groups=SizeGroup.class)	//250
	private String cookbook;

	@Column(name = "COOKBOOK_PAGE")
	private Integer cookbookPage;

	@Column(name = "MAGAZINE")
	@Size(max=250, groups=SizeGroup.class)	//250
	private String magazine;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="MM/dd/yyyy")
	@Column(name = "MAGAZINE_PUBDATE")
	@Past(groups=OtherGroup.class)
	private Date magazinePubdate;
	
	@Column(name = "NEWSPAPER")
	@Size(max=250, groups=SizeGroup.class)	//250
	private String newspaper;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="MM/dd/yyyy")
	@Column(name = "NEWSPAPER_PUBDATE")
	@Past(groups=OtherGroup.class)
	private Date newspaperPubdate;

	@Column(name = "PERSON")
	@Size(max=250, groups=SizeGroup.class)	//250
	private String person;
	
	@Column(name = "WEBSITE_URL")
	@URL(groups=OtherGroup.class)
	private String websiteUrl;
	
	@Column(name = "RECIPE_URL")
	@URL(groups=OtherGroup.class)
	private String recipeUrl;

	@Column(name = "OTHER")
	@Size(max=500, groups=SizeGroup.class)	//250
	private String other;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID", nullable = false)
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
	
	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cookbook == null) ? 0 : cookbook.hashCode());
		result = prime * result + ((cookbookPage == null) ? 0 : cookbookPage.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((magazine == null) ? 0 : magazine.hashCode());
		result = prime * result + ((magazinePubdate == null) ? 0 : magazinePubdate.hashCode());
		result = prime * result + ((newspaper == null) ? 0 : newspaper.hashCode());
		result = prime * result + ((newspaperPubdate == null) ? 0 : newspaperPubdate.hashCode());
		result = prime * result + ((other == null) ? 0 : other.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		result = prime * result + ((recipeUrl == null) ? 0 : recipeUrl.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((websiteUrl == null) ? 0 : websiteUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Source other = (Source) obj;
		if (cookbook == null) {
			if (other.cookbook != null)
				return false;
		} else if (!cookbook.equals(other.cookbook))
			return false;
		if (cookbookPage == null) {
			if (other.cookbookPage != null)
				return false;
		} else if (!cookbookPage.equals(other.cookbookPage))
			return false;
		if (id != other.id)
			return false;
		if (magazine == null) {
			if (other.magazine != null)
				return false;
		} else if (!magazine.equals(other.magazine))
			return false;
		if (magazinePubdate == null) {
			if (other.magazinePubdate != null)
				return false;
		} else if (!magazinePubdate.equals(other.magazinePubdate))
			return false;
		if (newspaper == null) {
			if (other.newspaper != null)
				return false;
		} else if (!newspaper.equals(other.newspaper))
			return false;
		if (newspaperPubdate == null) {
			if (other.newspaperPubdate != null)
				return false;
		} else if (!newspaperPubdate.equals(other.newspaperPubdate))
			return false;
		if (this.other == null) {
			if (other.other != null)
				return false;
		} else if (!this.other.equals(other.other))
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		if (recipeUrl == null) {
			if (other.recipeUrl != null)
				return false;
		} else if (!recipeUrl.equals(other.recipeUrl))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (websiteUrl == null) {
			if (other.websiteUrl != null)
				return false;
		} else if (!websiteUrl.equals(other.websiteUrl))
			return false;
		return true;
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