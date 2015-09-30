package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Instruction;
 
public interface InstructionService {
     
    public void addInstruction(Instruction instruction);
    public void updateInstruction(Instruction instruction);
    public void deleteInstruction(Long recipeID, int seqNo);
    public List<Instruction> listInstruction();
}