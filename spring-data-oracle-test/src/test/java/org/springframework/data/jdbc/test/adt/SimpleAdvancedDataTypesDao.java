package org.springframework.data.jdbc.test.adt;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;

import org.springframework.data.jdbc.support.oracle.*;

/**
 * @author trisberg
 */
@Repository
public class SimpleAdvancedDataTypesDao implements AdvancedDataTypesDao {

    private SimpleJdbcCall addSqlActorCall;

    private SimpleJdbcCall getSqlActorCall;

    private SimpleJdbcCall addActorCall;

    private SimpleJdbcCall getActorCall;
    
    private SimpleJdbcCall getActorNamesCall;

    private SimpleJdbcCall deleteActorsCall;

    private SimpleJdbcCall readActorsCall;

    @Autowired
    public void init(DataSource dataSource) {

        this.addSqlActorCall =
                new SimpleJdbcCall(dataSource).withProcedureName("add_actor")
                    .declareParameters(new SqlParameter("in_actor", OracleTypes.STRUCT, "ACTOR_TYPE"));

        this.getSqlActorCall =
                new SimpleJdbcCall(dataSource).withProcedureName("get_actor")
                    .declareParameters(
                        new SqlOutParameter("out_actor", OracleTypes.STRUCT, "ACTOR_TYPE",
                            new SqlReturnSqlData(SqlActor.class))
                    );

        this.addActorCall =
                new SimpleJdbcCall(dataSource).withProcedureName("add_actor")
                    .declareParameters(new SqlParameter("in_actor", OracleTypes.STRUCT, "ACTOR_TYPE"));

        this.getActorCall =
                new SimpleJdbcCall(dataSource).withProcedureName("get_actor")
                    .declareParameters(
                        new SqlOutParameter("out_actor", OracleTypes.STRUCT, "ACTOR_TYPE",
                                new SqlReturnStruct(Actor.class))
                );

        this.getActorNamesCall =
                new SimpleJdbcCall(dataSource).withFunctionName("get_actor_names")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(new SqlOutParameter("return", Types.ARRAY, "ACTOR_NAME_ARRAY",
                            new SqlReturnArray()));

        this.deleteActorsCall =
                new SimpleJdbcCall(dataSource).withProcedureName("delete_actors")
                	.withoutProcedureColumnMetaDataAccess()
                    .declareParameters(new SqlParameter("in_actor_ids", OracleTypes.ARRAY, "ACTOR_ID_ARRAY"));

        this.readActorsCall =
                new SimpleJdbcCall(dataSource).withProcedureName("read_actors")
                    .returningResultSet("out_actors_cur",
                            ParameterizedBeanPropertyRowMapper.newInstance(Actor.class));
    }

    @SuppressWarnings("unchecked")
	public void addSqlActor(final SqlActor actor) {
        Map in = Collections.singletonMap("in_actor", actor);
        addSqlActorCall.execute(in);
    }

	public SqlActor getSqlActor(Long id) {
        Map<String, Object> in = new HashMap<String, Object>();
        in.put("in_actor_id", id);
        return getSqlActorCall.executeObject(SqlActor.class, in);
    }

	public void addActor(final Actor actor) {
    	Map<String, Object> in = new HashMap<String, Object>();
    	in.put("in_actor", new SqlStructValue(actor));
        addActorCall.execute(in);
    }

	public Actor getActor(Long id) {
    	Map<String, Object> in = new HashMap<String, Object>();
        in.put("in_actor_id", id);
        return getActorCall.executeObject(Actor.class, in);
    }


	public String[] getActorNames() {
    	Map<String, Object> in = new HashMap<String, Object>();
        return getActorNamesCall.executeFunction(String[].class, in);
    }

	public void deleteActors(final Long[] ids) {
    	Map<String, Object> in = new HashMap<String, Object>();
    	in.put("in_actor_ids", new SqlArrayValue(ids));
        deleteActorsCall.execute(in);
    }


    @SuppressWarnings("unchecked")
	public List<Actor> getActors() {
        return readActorsCall.executeObject(List.class, Collections.emptyMap());
    }
}
