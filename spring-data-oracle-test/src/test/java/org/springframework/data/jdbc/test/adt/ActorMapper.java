package org.springframework.data.jdbc.test.adt;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.springframework.data.jdbc.support.oracle.StructMapper;

import java.sql.Connection;
import java.sql.SQLException;

public class ActorMapper implements StructMapper<Actor> {
	public STRUCT toStruct(Actor source, Connection conn, String typeName) throws SQLException {
		StructDescriptor descriptor = new StructDescriptor(typeName, conn);
		Object[] values = new Object[3];
		values[0] = source.getId();
		values[1] = source.getName();
		values[2] = source.getAge();
		return new STRUCT(descriptor, conn, values);
	}

	public Actor fromStruct(STRUCT struct) throws SQLException {
		Actor a = new Actor();
		Object[] attributes = struct.getAttributes();
		a.setId(Long.valueOf(((Number) attributes[0]).longValue()));
		a.setName(String.valueOf(attributes[1]));
		a.setAge(Integer.valueOf(((Number) attributes[2]).intValue()));
		return a;
	}
}
