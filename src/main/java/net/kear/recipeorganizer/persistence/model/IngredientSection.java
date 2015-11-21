package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import net.kear.recipeorganizer.persistence.model.RecipeIngredient;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "INGREDIENT_SECTION")
public class IngredientSection implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	public interface MinSizeGroup {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class,MinSizeGroup.class,RecipeIngredient.RecipeIngredientGroup.class})
	public interface IngredSectGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INGREDIENT_SECTION_SEQ")
	@SequenceGenerator(name = "INGREDIENT_SECTION_SEQ", sequenceName = "INGREDIENT_SECTION_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "SEQUENCE_NO", nullable = false)
	private int sequenceNo;
	
	@Column(name = "NAME")
	@NotBlank(groups=NotBlankGroup.class)
	@Size(max=50, groups=SizeGroup.class)	//50
	private String name;

	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="SECTION_ID", nullable=false)
	@Valid
	@Size(min=1, groups=MinSizeGroup.class)
	private List<RecipeIngredient> recipeIngredients = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);
	
	/*@Transient
	private List<Ingredient> ingredients;*/
	
	public IngredientSection() {}
	
	public IngredientSection(int seqNo, String name) {
		this.sequenceNo = seqNo;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	/*public List<Ingredient> getIngredients() {
		return ingredients;
	}
	
	public Ingredient getIngredient(int ndx) {
		return this.ingredients.get(ndx);
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public void setIngredient(int ndx, Ingredient ingredient) {
		this.ingredients.set(ndx, ingredient);
	}
	
	public void addIngredient(Ingredient ingredient) {
		this.ingredients.add(ingredient);
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + sequenceNo;
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
		IngredientSection other = (IngredientSection) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (id != other.id)
			return false;
		if (sequenceNo != other.sequenceNo)
			return false;
		if (recipeIngredients == null) {
			if (other.recipeIngredients != null)
				return false;
		} else if (!recipeIngredients.equals(other.recipeIngredients))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InstructionSection [id=" + id  
				+ ", sequenceNo=" + sequenceNo 
				+ ", name=" + name + ", recipeIngredients=" + recipeIngredients + "]";
	}
}