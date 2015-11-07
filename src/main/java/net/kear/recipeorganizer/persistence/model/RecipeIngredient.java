package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.GroupSequence;
import javax.validation.constraints.*;

import net.kear.recipeorganizer.persistence.model.Ingredient;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "RECIPE_INGREDIENTS")
public class RecipeIngredient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class,Ingredient.IngredientGroup.class})
	public interface RecipeIngredientGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_INGREDIENTS_SEQ")
	@SequenceGenerator(name = "RECIPE_INGREDIENTS_SEQ", sequenceName = "RECIPE_INGREDIENTS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "INGREDIENT_ID", nullable = false)
	@Min(value=1, groups=SizeGroup.class)
	private long ingredientId;
		
	@Column(name = "QUANTITY")
	@NotBlank(groups=NotBlankGroup.class)
	@Size(max=20, groups=SizeGroup.class)
	private String quantity;

	@Column(name = "QTY_AMT")
	private float qtyAmt;
	
	@Column(name = "QTY_TYPE")
	@Size(max=50, groups=SizeGroup.class)
	private String qtyType;

	@Column(name = "QUALIFIER")
	@Size(max=250, groups=SizeGroup.class)
	private String qualifier;

	@Column(name = "SEQUENCE_NO")
	private int sequenceNo;
	
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "INGREDIENT_ID", nullable = false, insertable = false, updatable = false)
	private Ingredient ingredient;
	
	public RecipeIngredient() {}
			
	public RecipeIngredient(long ingredientId, String quantity, float qtyAmt, String qtyType, String qualifier) {
		super();
		this.ingredientId = ingredientId;
		this.quantity = quantity;
		this.qtyAmt = qtyAmt;
		this.qtyType = qtyType;
		this.qualifier = qualifier;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(long ingredientId) {
		this.ingredientId = ingredientId;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public float getQtyAmt() {
		return qtyAmt;
	}

	public void setQtyAmt(float qtyAmt) {
		this.qtyAmt = qtyAmt;
	}

	public String getQtyType() {
		return qtyType;
	}

	public void setQtyType(String qtyType) {
		this.qtyType = qtyType;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
		result = prime * result + (int) (ingredientId ^ (ingredientId >>> 32));
		result = prime * result + Float.floatToIntBits(qtyAmt);
		result = prime * result + ((qtyType == null) ? 0 : qtyType.hashCode());
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
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
		RecipeIngredient other = (RecipeIngredient) obj;
		if (id != other.id)
			return false;
		if (ingredient == null) {
			if (other.ingredient != null)
				return false;
		} else if (!ingredient.equals(other.ingredient))
			return false;
		if (ingredientId != other.ingredientId)
			return false;
		if (Float.floatToIntBits(qtyAmt) != Float.floatToIntBits(other.qtyAmt))
			return false;
		if (qtyType == null) {
			if (other.qtyType != null)
				return false;
		} else if (!qtyType.equals(other.qtyType))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (sequenceNo != other.sequenceNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeIngredient [id=" + id 
				+ ", ingredientId=" + ingredientId 
				+ ", quantity=" + quantity 
				+ ", qtyAmt=" + qtyAmt 
				+ ", qtyType=" + qtyType 
				+ ", qualifier=" + qualifier
				+ ", sequenceNo=" + sequenceNo 
				+ ", ingredient=" + ingredient + "]";
	}
}