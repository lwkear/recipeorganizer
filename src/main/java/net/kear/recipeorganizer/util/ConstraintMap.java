package net.kear.recipeorganizer.util;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import javax.validation.metadata.Scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConstraintMap {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getModelConstraint(String constraint, String attribute, Class clazz) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Map<String, Object> constraintMap = new HashMap<String, Object>();

		BeanDescriptor userDescriptor = validator.getConstraintsForClass(clazz);		
		Set<PropertyDescriptor> propSet = userDescriptor.getConstrainedProperties();
		for (PropertyDescriptor propDesc : propSet) {
			Set<ConstraintDescriptor<?>> fieldCon = propDesc
					.findConstraints()
					.lookingAt(Scope.LOCAL_ELEMENT)
					.declaredOn(ElementType.FIELD)
					.getConstraintDescriptors();
			for (ConstraintDescriptor<?> conDesc : fieldCon) {
				String annotation = conDesc.getAnnotation().toString();
				Map<String, Object> attr = conDesc.getAttributes();
				Set<String> keys = attr.keySet();
				for (String key : keys) {
					Object value = attr.get(key);
					if ((annotation.indexOf(constraint) >= 0) && key.equals(attribute)) {
						String fieldKey = propDesc.getPropertyName() + "." + key;
						constraintMap.put(fieldKey, value);
					}
				}
			}
		}

		Set<Entry<String, Object>> entries = constraintMap.entrySet();
		for (Entry<String, Object> entry : entries)
			logger.debug("key/value= " + entry.getKey() + "/" + entry.getValue());
		
		return constraintMap;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getModelConstraints(String constraint, String attribute, Class...classes) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Map<String, Object> constraintMap = new HashMap<String, Object>();
		
		for (Class clazz : classes) {
			String className = clazz.getSimpleName();
			BeanDescriptor userDescriptor = validator.getConstraintsForClass(clazz);
			Set<PropertyDescriptor> propSet = userDescriptor.getConstrainedProperties();
			for (PropertyDescriptor propDesc : propSet) {
				String propName = propDesc.getPropertyName();
				Set<ConstraintDescriptor<?>> fieldCon = propDesc
						.findConstraints()
						.lookingAt(Scope.LOCAL_ELEMENT)
						.declaredOn(ElementType.FIELD)
						.getConstraintDescriptors();
				for (ConstraintDescriptor<?> conDesc : fieldCon) {
					String annotation = conDesc.getAnnotation().toString();
					Map<String, Object> attr = conDesc.getAttributes();
					Set<String> keys = attr.keySet();
					for (String key : keys) {
						Object value = attr.get(key);
						if ((annotation.indexOf(constraint) >= 0) && key.equals(attribute)) {
							String fieldKey = className + "." + propName  + "." + key;
							constraintMap.put(fieldKey, value);
						}
					}
				}
			}
		}
		
		Set<Entry<String, Object>> entries = constraintMap.entrySet();
		for (Entry<String, Object> entry : entries)
			logger.debug("key/value= " + entry.getKey() + "/" + entry.getValue());
		
		return constraintMap;
	}
}
