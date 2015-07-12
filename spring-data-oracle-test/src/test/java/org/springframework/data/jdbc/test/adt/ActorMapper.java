package org.springframework.data.jdbc.test.adt;

import org.springframework.data.jdbc.support.oracle.StructMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

public class ActorMapper implements StructMapper<Actor> {
	public Struct toStruct(Actor source, Connection conn, String typeName) throws SQLException {
		Object[] values = new Object[3];
		values[0] = source.getId();
		values[1] = source.getName();
		values[2] = source.getAge();
		return conn.createStruct(typeName, values);
	}

	public Actor fromStruct(Struct struct) throws SQLException {
		Actor a = new Actor();
		Object[] attributes = struct.getAttributes();
		a.setId(Long.valueOf(((Number) attributes[0]).longValue()));
		a.setName(String.valueOf(attributes[1]));
		a.setAge(Integer.valueOf(((Number) attributes[2]).intValue()));
		return a;
	}
}
