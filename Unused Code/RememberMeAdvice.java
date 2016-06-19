package net.kear.recipeorganizer.advice;

import java.sql.SQLException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.GenericJDBCException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
//@Order(value=1)
public class RememberMeAdvice {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	//@Around("execution(* net.kear.recipeorganizer.security.UserSecurityService.loadUserByUsername(..))")
	@Around("execution(* net.kear.recipeorganizer.persistence.repository.UserRepository.*(..))")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Object obj = null;
		try {
			obj = pjp.proceed();
			return obj;
		} catch (SQLException ex) {
			logger.debug("aspect @Around");
			logger.error(ex.getClass().toString(), ex);
			//throw new UsernameNotFoundException("Error retrieving user", ex);
			throw new GenericJDBCException("DB Exception",ex);
		}
	}
}
