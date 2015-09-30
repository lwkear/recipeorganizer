package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.model.Instruction;
 
public interface InstructionRepository {

    public void addInstruction(Instruction instruction);
    public void updateInstruction(Instruction instruction);
    public void deleteInstruction(Long recipeID, int seqNo);
    public List<Instruction> listInstruction();
    
}
