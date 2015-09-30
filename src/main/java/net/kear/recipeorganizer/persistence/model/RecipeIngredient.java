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
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import net.kear.recipeorganizer.persistence.model.Ingredient;

@Entity
@Table(name = "RECIPE_INGREDIENTS")
public class RecipeIngredient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_INGREDIENTS_SEQ")
	@SequenceGenerator(name = "RECIPE_INGREDIENTS_SEQ", sequenceName = "RECIPE_INGREDIENTS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "INGREDIENT_ID", nullable = false)
	@NotNull
	@Min(1)
	private long ingredientId;
		
	@Column(name = "QUANTITY")
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(max=20)
	private String quantity;

	@Column(name = "QTY_AMT")
	private float qtyAmt;
	
	@Column(name = "QTY_TYPE")
	@Size(max=50)
	private String qtyType;

	@Column(name = "QUALIFIER")
	@Size(max=250)
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