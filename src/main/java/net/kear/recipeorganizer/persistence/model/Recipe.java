package net.kear.recipeorganizer.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.Valid;
import javax.validation.constraints.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.constraints.*;
import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.util.TagList;

@Entity
@Table(name = "RECIPE")
@TypeDefs({
	@TypeDef(name = "tagList", typeClass = TagList.class)
})
public class Recipe implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_SEQ")
	@SequenceGenerator(name = "RECIPE_SEQ", sequenceName = "RECIPE_SEQ", allocationSize = 1)
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;
	
	@Column(name = "NAME", nullable = false)
	@NotNull
	@NotBlank 
	@NotEmpty 
	@Size(max=250)
	private String name;

	@Column(name = "BACKGROUND")
	@Lob
	private String background;
	
	@Column(name = "DESCRIPTION")
	@Lob
	private String description;

	@Column(name = "SERVINGS")
	@Size(max=100)
	private String servings;

	@Column(name = "NOTES")
	@Lob
	private String notes;

	@Column(name = "RATING")
	@Min(0)
	@Max(5)
	private int rating;

	@Column(name = "FAVORITE")
	private boolean favorite;

	@Column(name = "ALLOW_SHARE")
	private boolean allowShare;

	@Column(name = "LAST_MADE")
	@Past
	private Date lastMade;

	@Column(name = "MADE_COUNT")
	private int madeCount;

	@Column(name = "PHOTO")
	private String photo;

	@Column(name = "TAGS")
	@Type(type = "tagList") 
	@Size(max=10)
	private List<String> tags;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
	@Valid
	private Category category;
	
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	@NotNull @Size(min=1)	
	private List<Instruction> instructions = new AutoPopulatingList<Instruction>(Instruction.class);
	
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	@NotNull
	@Size(min=1)	
	private List<RecipeIngredient> recipeIngredients = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);

	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="RECIPE_ID", nullable=false)
	@Valid
	private List<Source> sources = new ArrayList<Source>();
	
	public Recipe() {}

	public Recipe(User user, String name, String background, String description, Category category, String servings, String notes, int rating, boolean favorite, boolean allowShare, Date lastMade,			
			int madeCount, String photo, List<String> tags, List<Instruction> instructions, List<RecipeIngredient> recipeIngredients, List<Source> sources) {
		super();
		this.user = user;
		this.name = name;
		this.background = background;
		this.description = description;
		this.category = category;
		this.servings = servings;
		this.notes = notes;
		this.rating = rating;
		this.favorite = favorite;
		this.allowShare = allowShare;
		this.lastMade = lastMade;
		this.madeCount = madeCount;
		this.photo = photo;
		this.tags = tags;
		this.instructions = instructions;
		this.recipeIngredients = recipeIngredients;
		this.sources = sources;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Boolean getFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	public Boolean getAllowShare() {
		return allowShare;
	}

	public void setAllowShare(Boolean allowShare) {
		this.allowShare = allowShare;
	}

	public Date getLastMade() {
		return lastMade;
	}

	public void setLastMade(Date lastMade) {
		this.lastMade = lastMade;
	}

	public int getMadeCount() {
		return madeCount;
	}

	public void setMadeCount(int madeCount) {
		this.madeCount = madeCount;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
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

	public List<Source> getSources() {
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
	
	@Override
	public String toString() {
		return "Recipe [id=" + id 
				+ ", user=" + user
				+ ", name=" + name 
				+ ", background=" + background 
				+ ", description=" + description 
				+ ", category=" + category
				+ ", servings=" + servings
				+ ", notes=" + notes 
				+ ", rating=" + rating 
				+ ", favorite=" + favorite 
				+ ", allowShare=" + allowShare 
				+ ", lastMade=" + lastMade 
				+ ", madeCount=" + madeCount 
				+ ", photo=" + photo
				+ ", tags=" + tags 
				+ ", instructions=" + instructions 
				+ ", recipeIngredients=" + recipeIngredients
				+ ", source=" + sources + "]";
	}	
}