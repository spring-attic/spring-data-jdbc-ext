package org.springframework.data.jdbc.test.adt;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import org.springframework.data.jdbc.support.oracle.SqlArrayValue;
import org.springframework.data.jdbc.support.oracle.SqlReturnArray;
import org.springframework.data.jdbc.support.oracle.SqlReturnSqlData;
import org.springframework.data.jdbc.support.oracle.SqlReturnStruct;
import org.springframework.data.jdbc.support.oracle.SqlStructValue;

/**
 * @author trisberg
 */
@Repository
public class ClassicAdvancedDataTypesDao implements AdvancedDataTypesDao {

    private AddSqlActorProc addSqlActorProc;

    private GetSqlActorProc getSqlActorProc;

    private AddActorProc addActorProc;

    private GetActorProc getActorProc;

    private GetActorNamesProc getActorNamesProc;

    private DeleteActorsProc deleteActorsProc;

    private ReadActorsProc readActorsProc;

    @Autowired
    public void init(DataSource dataSource) {

        this.addSqlActorProc =
                new AddSqlActorProc(dataSource);

        this.getSqlActorProc =
                new GetSqlActorProc(dataSource);

        this.addActorProc =
                new AddActorProc(dataSource);

        this.getActorProc =
                new GetActorProc(dataSource);

        this.getActorNamesProc =
                new GetActorNamesProc(dataSource);

        this.deleteActorsProc =
                new DeleteActorsProc(dataSource);

        this.readActorsProc =
                new ReadActorsProc(dataSource);
    }

    public void addSqlActor(final SqlActor actor) {
        addSqlActorProc.execute(actor);
    }

	public SqlActor getSqlActor(Long id) {
        return getSqlActorProc.execute(id);
    }

    public void addActor(final Actor actor) {
        addActorProc.execute(actor);
    }

    public Actor getActor(Long id) {
        return getActorProc.execute(id);
    }

    public String[] getActorNames() {
        return getActorNamesProc.execute();
    }

    public void deleteActors(final Long[] ids) {
        deleteActorsProc.execute(ids);
    }

    @SuppressWarnings("unchecked")
	public List<Actor> getActors() {
        return readActorsProc.execute();
    }

    private class AddSqlActorProc extends StoredProcedure {

        public AddSqlActorProc(DataSource dataSource) {
            super(dataSource, "add_actor");
            declareParameter(new SqlParameter("in_actor", OracleTypes.STRUCT, "ACTOR_TYPE"));
        }

        @SuppressWarnings("unchecked")
		public void execute(Actor actor) {
            Map in = Collections.singletonMap("in_actor", actor);
            this.execute(in);
        }

    }

    private class GetSqlActorProc extends StoredProcedure {

        public GetSqlActorProc(DataSource dataSource) {
            super(dataSource, "get_actor");
            declareParameter(new SqlParameter("in_actor_id", Types.NUMERIC));
            declareParameter(
                new SqlOutParameter("out_actor", OracleTypes.STRUCT, "ACTOR_TYPE",
                    new SqlReturnSqlData(SqlActor.class))
            );
        }

        @SuppressWarnings("unchecked")
		public SqlActor execute(Long id) {
            Map in = Collections.singletonMap("in_actor_id", id);
            Map out = this.execute(in);
            return (SqlActor) out.get("out_actor");
        }

    }

    private class AddActorProc extends StoredProcedure {

        public AddActorProc(DataSource dataSource) {
            super(dataSource, "add_actor");
            declareParameter(new SqlParameter("in_actor", OracleTypes.STRUCT, "ACTOR_TYPE"));
        }

        @SuppressWarnings("unchecked")
		public void execute(Actor actor) {
            Map in = Collections.singletonMap("in_actor", new SqlStructValue(actor));
            this.execute(in);
        }

    }

    private class GetActorProc extends StoredProcedure {

        public GetActorProc(DataSource dataSource) {
            super(dataSource, "get_actor");
            declareParameter(new SqlParameter("in_actor_id", Types.NUMERIC));
            declareParameter(
                new SqlOutParameter("out_actor", OracleTypes.STRUCT, "ACTOR_TYPE",
                        new SqlReturnStruct(Actor.class))
            );
        }

        @SuppressWarnings("unchecked")
		public Actor execute(Long id) {
            Map in = Collections.singletonMap("in_actor_id", id);
            Map out = this.execute(in);
            return (Actor) out.get("out_actor");
        }

    }

    private class GetActorNamesProc extends StoredProcedure {

        public GetActorNamesProc(DataSource dataSource) {
            super(dataSource, "get_actor_names");
            setFunction(true);
            declareParameter(new SqlOutParameter("return", Types.ARRAY, "ACTOR_NAME_ARRAY",
                    new SqlReturnArray()));
        }

        @SuppressWarnings("unchecked")
		public String[] execute() {
            Map in = Collections.emptyMap();
            Map out = this.execute(in);
            return (String[]) out.get("return");
        }

    }

    private class DeleteActorsProc extends StoredProcedure {

        public DeleteActorsProc(DataSource dataSource) {
            super(dataSource, "delete_actors");
            declareParameter(new SqlParameter("in_actor_ids", OracleTypes.ARRAY, "ACTOR_ID_ARRAY"));
        }

        @SuppressWarnings("unchecked")
		public void execute(Long[] ids) {
            Map in = Collections.singletonMap("in_actor_ids", new SqlArrayValue(ids));
            this.execute(in);
        }

    }

    private class ReadActorsProc extends StoredProcedure {

        public ReadActorsProc(DataSource dataSource) {
            super(dataSource, "read_actors");
            declareParameter(
                new SqlOutParameter("out_actors_cur", OracleTypes.CURSOR,
                        ParameterizedBeanPropertyRowMapper.newInstance(Actor.class))
            );

        }

        @SuppressWarnings("unchecked")
		public List execute() {
            Map in = Collections.emptyMap();
            Map out = this.execute(in);
            return (List) out.get("out_actors_cur");
        }

    }
}
