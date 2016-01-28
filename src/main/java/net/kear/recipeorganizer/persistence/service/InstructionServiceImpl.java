package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.repository.InstructionRepository;
import net.kear.recipeorganizer.persistence.service.InstructionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class InstructionServiceImpl implements InstructionService {
 
    @Autowired
    private InstructionRepository instructionRepository;
      
    public void addInstruction(Instruction instruction) {
    	instructionRepository.addInstruction(instruction);
    }
    
    public void updateInstruction(Instruction instruction) {
    	instructionRepository.updateInstruction(instruction);
    }
 
    public List<Instruction> listInstruction() {
    	return instructionRepository.listInstruction();
    }
}