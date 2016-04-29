package net.kear.recipeorganizer.util.db;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.engine.spi.SessionImplementor;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresTagList implements UserType {

	@Override
	public int[] sqlTypes() {
	    return new int[]{Types.ARRAY};
	}
	
	@Override
	public Class<?> returnedClass() {
	    return List.class;
	}
	
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
	    if (x == null && y == null) return true;
	    else if (x == null && y != null) return false;
	    else return x.equals(y);
	}
	
	@Override
	public int hashCode(Object x) throws HibernateException {
	    return x.hashCode();
	}
	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {	
		
		Array array = (Array) rs.getArray(names[0]);
		List<String> strList;
		if (array != null) {
			strList = Arrays.asList((String[]) array.getArray());
		}
		else {
			String[] str = new String[] {""};
			strList = Arrays.asList(str);
		}			
			
		return strList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {

	    if (value != null) {
	    	List<String> str = (List<String>) value;
	    	Array array = session.connection().createArrayOf("varchar", str.toArray());
	        st.setObject(index, array);
	    }
	    else {
	    	st.setNull(index, sqlTypes()[0]);
	    }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object deepCopy(Object value) throws HibernateException {
	    if (value == null) return null;
	
	    return new ArrayList<String>((List<String>) value);
	}
	
	@Override
	public boolean isMutable() {
	    return false;
	}
	
	public Object assemble(Serializable _cached, Object _owner) throws HibernateException {
	    return _cached;
	}
	
	public Serializable disassemble(Object _obj) throws HibernateException {
	    return (Serializable) _obj;
	}
	
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
	    return deepCopy(original);
	}
}