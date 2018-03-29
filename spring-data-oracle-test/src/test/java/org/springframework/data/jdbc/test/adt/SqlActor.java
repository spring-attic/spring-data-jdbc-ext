package org.springframework.data.jdbc.test.adt;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 * @author trisberg
 */
public class SqlActor extends Actor implements SQLData {

    public String getSQLTypeName() {
        return "ACTOR_TYPE";
    }

    public void readSQL(SQLInput sqlInput, String string) throws SQLException {
        setId(Long.valueOf(sqlInput.readLong()));
        setName(sqlInput.readString());
        setAge(sqlInput.readInt());
    }

    public void writeSQL(SQLOutput sqlOutput) throws SQLException {
        sqlOutput.writeLong(getId().longValue());
        sqlOutput.writeString(getName());
        sqlOutput.writeInt(getAge());
    }

}
