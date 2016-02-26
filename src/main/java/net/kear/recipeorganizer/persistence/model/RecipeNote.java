package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "RECIPE_NOTES")
public class RecipeNote implements Serializable {

	private static final long serialVersionUID = 1L;	

	@EmbeddedId
	private RecipeNoteKey id = new RecipeNoteKey();
	
	@Column(name = "NOTE")
	@Lob
	private String note;
	
	public RecipeNote() {}
	
	public RecipeNote(RecipeNoteKey id, String note) {
		super();
		this.id = id;
		this.note = note;
	}

	public RecipeNoteKey getId() {
		return id;
	}

	public void setId(RecipeNoteKey id) {
		this.id = id;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RecipeNote other = (RecipeNote) obj;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeNote [id=" + id 
				+ ", note=" + note + "]";
	}
}
