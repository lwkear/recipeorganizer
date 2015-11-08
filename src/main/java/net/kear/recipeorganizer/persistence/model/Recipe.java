package net.kear.recipeorganizer.persistence.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.GroupSequence;

import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.util.TagList;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.constraints.*;
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
	
	@GroupSequence({MinSizeGroup1.class, Instruction.InstructGroup.class})
	public interface RecipeInstructGroup {}
	
	@GroupSequence({MinSizeGroup2.class, RecipeIngredient.RecipeIngredientGroup.class})
	public interface RecipeRecipeIngredientGroup {}
	
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
	@Size(max=10, groups=SizeGroup.class)	//250
	private String name;

	@Column(name = "DESCRIPTION")
	@NotBlank(groups=SizeGroup.class)
	@Lob
	private String description;

	@Column(name = "SERVINGS")
	@Size(max=10, groups=SizeGroup.class)	//100
	private String servings;

	@Column(name = "PREP_TIME")
	private int prepTime;

	@Column(name = "ALLOW_SHARE")
	private boolean allowShare;

	@ManyToOne(optional = false)
	@JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
	@Valid
	private Category category;

	/*** ingredients page ***/
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	@Size(min=1, groups=MinSizeGroup2.class)
	private List<RecipeIngredient> recipeIngredients = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);

	/*** instructions page ***/
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	@Size(min=1, groups=MinSizeGroup1.class)
	private List<Instruction> instructions = new AutoPopulatingList<Instruction>(Instruction.class);
	
	/*** optional page ***/
	@Column(name = "BACKGROUND")
	@Lob
	private String background;
	
	@Column(name = "NOTES")
	@Lob
	private String notes;

	@Column(name = "PHOTO")
	private String photo;

	@Column(name = "TAGS")
	@Type(type = "tagList") 
	@Size(max=10, groups=SizeGroup.class)
	private List<String> tags;
	
	/*@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	private List<Source> sources = new ArrayList<Source>();*/
	
	//@OneToOne(mappedBy = "recipe", orphanRemoval=true, optional = true, fetch = FetchType.LAZY)
	//private Source source;
	
    @OneToOne(orphanRemoval=true, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID", nullable = false)
    private Source source;

	
	public Recipe() {}

	public Recipe(User user, String name, String background, String description, Category category, String servings, Integer prepTime, String notes, 
				boolean allowShare, String photo, List<String> tags, List<Instruction> instructions, List<RecipeIngredient> recipeIngredients, 
				/*List<Source> sources) {*/
				Source source) {
		super();
		this.user = user;
		this.name = name;
		this.background = background;
		this.description = description;
		this.category = category;
		this.servings = servings;
		this.prepTime = prepTime;
		this.notes = notes;
		this.allowShare = allowShare;
		this.photo = photo;
		this.tags = tags;
		this.instructions = instructions;
		this.recipeIngredients = recipeIngredients;
		/*this.sources = sources;*/
		this.source = source;
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

	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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

	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public Instruction getInstruction(int ndx) {
		return this.instructions.get(ndx);
	}

	public void setInstruction(List<Instruction> instructions) {
		this.instructions = instructions;
	}

	public void setInstruction(int ndx, Instruction instruction) {
		this.instructions.set(ndx, instruction);
	}
	
	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}
	
	public List<RecipeIngredient> getRecipeIngredients() {
		return recipeIngredients;
	}

	public RecipeIngredient getRecipeIngredient(int ndx) {
		return this.recipeIngredients.get(ndx);
	}
	
	public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
	}

	public void setRecipeIngredient(int ndx, RecipeIngredient recipeIngredient) {
		this.recipeIngredients.set(ndx, recipeIngredient);
	}
	
	public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
		this.recipeIngredients.add(recipeIngredient);
	}

	/*public List<Source> getSources() {
		return sources;
	}

	public Source getSource(int ndx) {
		return this.sources.get(ndx);
	}
	public void setSources(List<Source> sources) {
		this.sources = sources;
	}

	public void setSource(int ndx, Source source) {
		this.sources.set(ndx, source);
	}

	public void addSource(Source source) {
		this.sources.add(source);
	}
	*/

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
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
		result = prime * result + ((instructions == null) ? 0 : instructions.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((photo == null) ? 0 : photo.hashCode());
		result = prime * result + prepTime;
		result = prime * result + ((recipeIngredients == null) ? 0 : recipeIngredients.hashCode());
		result = prime * result + ((servings == null) ? 0 : servings.hashCode());
		/*result = prime * result + ((sources == null) ? 0 : sources.hashCode());*/
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (instructions == null) {
			if (other.instructions != null)
				return false;
		} else if (!instructions.equals(other.instructions))
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
		if (photo == null) {
			if (other.photo != null)
				return false;
		} else if (!photo.equals(other.photo))
			return false;
		if (prepTime != other.prepTime)
			return false;
		if (recipeIngredients == null) {
			if (other.recipeIngredients != null)
				return false;
		} else if (!recipeIngredients.equals(other.recipeIngredients))
			return false;
		if (servings == null) {
			if (other.servings != null)
				return false;
		} else if (!servings.equals(other.servings))
			return false;
		/*if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;*/
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
				", servings=" + servings + ", prepTime=" + prepTime + ", notes=" + notes + ", allowShare=" + allowShare + ", photo=" + photo +
				", tags=" + tags + ", category=" + category + ", instructions=" + instructions + ", recipeIngredients=" + recipeIngredients + 
				/*", sources=" + sources + "]";*/
				", source=" + source + "]";
	}
}