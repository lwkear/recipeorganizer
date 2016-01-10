package net.kear.recipeorganizer.persistence.model;

import java.util.Date;
import java.util.List;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.GroupSequence;

import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.util.TagList;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "RECIPE")
@TypeDefs({
	@TypeDef(name = "tagList", typeClass = TagList.class)
})
public class Recipe implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	public interface MinSizeGroup1 {}
	public interface MinSizeGroup2 {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class,Category.CategoryGroup.class})
	public interface RecipeBasicGroup {}
	
	@GroupSequence({MinSizeGroup1.class, IngredientSection.IngredSectGroup.class})
	public interface RecipeIngredGroup {}

	@GroupSequence({MinSizeGroup2.class, InstructionSection.InstructSectGroup.class,})
	public interface RecipeInstructGroup {}
	
	@GroupSequence({SizeGroup.class, Source.SourceGroup.class})
	public interface RecipeOptionalGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_SEQ")
	@SequenceGenerator(name = "RECIPE_SEQ", sequenceName = "RECIPE_SEQ", allocationSize = 1)
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;

	/*** basics page ***/
	@Column(name = "NAME", nullable = false)
	@NotBlank(groups=SizeGroup.class) 
	@Size(max=250, groups=SizeGroup.class)	//250
	private String name;

	@Column(name = "DESCRIPTION")
	@NotBlank(groups=SizeGroup.class)
	@Lob
	private String description;

	@Column(name = "SERVINGS")
	@Size(max=100, groups=SizeGroup.class)	//100
	private String servings;

	@Column(name = "PREP_HOURS")
	private Integer prepHours;

	@Column(name = "PREP_MINUTES")
	@Max(value=59, groups=SizeGroup.class)
	private Integer prepMinutes;

	@Column(name = "ALLOW_SHARE")
	private boolean allowShare;

	@ManyToOne(optional = false)
	@JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
	@Valid
	private Category category;

	/*** ingredients page ***/
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@OrderBy("sequenceNo")
	@Valid
	@Size(min=1, groups=MinSizeGroup1.class)
	private List<IngredientSection> ingredSections = new AutoPopulatingList<IngredientSection>(IngredientSection.class);

	@Transient
	private int numIngredSections;

	@Transient
	private int currIngredSection;
	
	/*** instructions page ***/
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)	//mappedBy="recipe", 
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@OrderBy("sequenceNo")
	@Valid
	@Size(min=1, groups=MinSizeGroup2.class)
	private List<InstructionSection> instructSections = new AutoPopulatingList<InstructionSection>(InstructionSection.class);
	
	@Transient
	private int numInstructSections;

	@Transient
	private int currInstructSection;

	/*** optional page ***/
	@Column(name = "BACKGROUND")
	@Lob
	private String background;
	
	@Column(name = "NOTES")
	@Lob
	private String notes;
	
	@Column(name = "PHOTO")
	private String photoName;

	@Column(name = "TAGS")
	@Type(type = "tagList") 
	@Size(max=10, groups=SizeGroup.class)
	private List<String> tags;
	
	@OneToOne(mappedBy = "recipe", orphanRemoval=true, optional = true, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@Valid
	private Source source;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_ADDED", insertable=false, updatable=false)
	private Date dateAdded;
	
	@Column(name = "VIEWS")
	private Integer views;
	
	public Recipe() {}

	public Recipe(User user, String name, String background, String description, Category category, String servings, Integer prepHours, 
				Integer prepMinutes, String notes, boolean allowShare, String photoName, List<String> tags, 
				List<InstructionSection> instructSections, List<IngredientSection> ingredSections, Source source, Integer views) {
		super();
		this.user = user;
		this.name = name;
		this.background = background;
		this.description = description;
		this.category = category;
		this.servings = servings;
		this.prepHours = prepHours;
		this.prepMinutes = prepMinutes;
		this.notes = notes;
		this.allowShare = allowShare;
		this.photoName = photoName;
		this.tags = tags;
		this.instructSections = instructSections;
		this.ingredSections = ingredSections;
		this.source = source;
		this.views = views;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getServings() {
		return servings;
	}

	public void setServings(String servings) {
		this.servings = servings;
	}

	public Integer getPrepHours() {
		return prepHours;
	}

	public void setPrepHours(Integer prepHours) {
		this.prepHours = prepHours;
	}

	public Integer getPrepMinutes() {
		return prepMinutes;
	}

	public void setPrepMinutes(Integer prepMinutes) {
		this.prepMinutes = prepMinutes;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getAllowShare() {
		return allowShare;
	}

	public void setAllowShare(Boolean allowShare) {
		this.allowShare = allowShare;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	
	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<InstructionSection> getInstructSections() {
		return instructSections;
	}
	
	public InstructionSection getInstructionSection(int ndx) {
		return this.instructSections.get(ndx);
	}

	public void setInstructSections(List<InstructionSection> instructSections) {
		this.instructSections = instructSections;
	}

	public void setInstructionSection(int ndx, InstructionSection instructionSection) {
		this.instructSections.set(ndx, instructionSection);
	}
	
	public void addInstructionSection(InstructionSection instructionSection) {
		this.instructSections.add(instructionSection);
	}

	public Integer getNumInstructSections() {
		return numInstructSections;
	}

	public void setNumInstructSections(Integer numInstructSections) {
		this.numInstructSections = numInstructSections;
	}

	public Integer getCurrInstructSection() {
		return currInstructSection;
	}

	public void setCurrInstructSection(Integer currInstructSection) {
		this.currInstructSection = currInstructSection;
	}

	public List<IngredientSection> getIngredSections() {
		return ingredSections;
	}

	public IngredientSection getIngredientSection(int ndx) {
		return this.ingredSections.get(ndx);
	}
	
	public void setIngredSections(List<IngredientSection> ingredSections) {
		this.ingredSections = ingredSections;
	}

	public void setIngredientSection(int ndx, IngredientSection ingredSection) {
		this.ingredSections.set(ndx, ingredSection);
	}
	
	public void addIngredientSection(IngredientSection ingredSection) {
		this.ingredSections.add(ingredSection);
	}

	public Integer getNumIngredSections() {
		return numIngredSections;
	}

	public void setNumIngredSections(Integer numIngredSections) {
		this.numIngredSections = numIngredSections;
	}

	public Integer getCurrIngredSection() {
		return currIngredSection;
	}

	public void setCurrIngredSection(Integer currIngredSection) {
		this.currIngredSection = currIngredSection;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	
	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allowShare ? 1231 : 1237);
		result = prime * result + ((background == null) ? 0 : background.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((photoName == null) ? 0 : photoName.hashCode());
		result = prime * result + ((prepHours == null) ? 0 : prepHours.hashCode());
		result = prime * result + ((prepMinutes == null) ? 0 : prepMinutes.hashCode());
		result = prime * result + ((servings == null) ? 0 : servings.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		Recipe other = (Recipe) obj;
		if (allowShare != other.allowShare)
			return false;
		if (background == null) {
			if (other.background != null)
				return false;
		} else if (!background.equals(other.background))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (instructSections == null) {
			if (other.instructSections != null)
				return false;
		} else if (!instructSections.equals(other.instructSections))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (photoName == null) {
			if (other.photoName != null)
				return false;
		} else if (!photoName.equals(other.photoName))
			return false;
		if (prepHours == null) {
			if (other.prepHours != null)
				return false;
		} else if (!prepHours.equals(other.prepHours))
			return false;
		if (prepMinutes == null) {
			if (other.prepMinutes != null)
				return false;
		} else if (!prepMinutes.equals(other.prepMinutes))
			return false;
		if (ingredSections == null) {
			if (other.ingredSections != null)
				return false;
		} else if (!ingredSections.equals(other.ingredSections))
			return false;
		if (servings == null) {
			if (other.servings != null)
				return false;
		} else if (!servings.equals(other.servings))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", user=" + user + ", name=" + name + ", background=" + background + ", description=" + description +
				", servings=" + servings + ", prepHours=" + prepHours + ", prepMinutes=" + prepMinutes + ", notes=" + notes + ", allowShare=" + allowShare + 
				", photoName=" + photoName + ", tags=" + tags + ", category=" + category + ", instructSections=" + instructSections + ", ingredSections=" +  
				ingredSections + ", source=" + source + ", views=" + views + "]";
	}
}