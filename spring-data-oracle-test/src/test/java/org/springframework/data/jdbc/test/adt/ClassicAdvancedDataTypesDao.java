package org.springframework.data.jdbc.test.adt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jdbc.support.oracle.SqlReturnStructArray;
import org.springframework.data.jdbc.support.oracle.SqlStructArrayValue;
import org.springframework.data.jdbc.support.oracle.StructMapper;
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
@SuppressWarnings("unused")
public class ClassicAdvancedDataTypesDao implements AdvancedDataTypesDao {

    private AddSqlActorProc addSqlActorProc;

    private GetSqlActorProc getSqlActorProc;

    private AddActorProc addActorProc;

    private GetActorProc getActorProc;

    private GetActorNamesProc getActorNamesProc;

    private DeleteActorsProc deleteActorsProc;

    private ReadActorsProc readActorsProc;

    private GetActorArrayProc getActorArrayProc;

	private SaveActorArrayProc saveActorArrayProc;

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

		this.getActorArrayProc =
				new GetActorArrayProc(dataSource);

		this.saveActorArrayProc =
				new SaveActorArrayProc(dataSource);
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

	public List<Actor> getAllActors() {
		Object[] actors = getActorArrayProc.execute();
		List<Actor> result = new ArrayList<Actor>();
			for (Object actor : actors) {
				if (actor instanceof Actor) {
					result.add((Actor) actor);
				}
			}
		return result;
	}

	public void saveActors(List<Actor> actors) {
		saveActorArrayProc.execute(actors);
	}

	private class AddSqlActorProc extends StoredProcedure {

        public AddSqlActorProc(DataSource dataSource) {
            super(dataSource, "add_actor");
            declareParameter(new SqlParameter("in_actor", Types.STRUCT, "ACTOR_TYPE"));
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
                new SqlOutParameter("out_actor", Types.STRUCT, "ACTOR_TYPE",
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
            declareParameter(new SqlParameter("in_actor", Types.STRUCT, "ACTOR_TYPE"));
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
                new SqlOutParameter("out_actor", Types.STRUCT, "ACTOR_TYPE",
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
            declareParameter(new SqlParameter("in_actor_ids", Types.ARRAY, "ACTOR_ID_ARRAY"));
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

	private class GetActorArrayProc extends StoredProcedure {

		public GetActorArrayProc(DataSource dataSource) {
			super(dataSource, "get_all_actor_types");
			setFunction(true);
			declareParameter(new SqlOutParameter("return", Types.ARRAY, "ACTOR_ARRAY_TYPE",
					new SqlReturnStructArray<Actor>(
					  new StructMapper<Actor>() {
						  public Struct toStruct(Actor source, Connection conn, String typeName) throws SQLException {
							  throw new InvalidDataAccessApiUsageException("Not implemented");
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
			  )));
		}

		@SuppressWarnings("unchecked")
		public Object[] execute() {
			Map in = Collections.emptyMap();
			Map out = this.execute(in);
			return (Object[]) out.get("return");
		}

	}

	private class SaveActorArrayProc extends StoredProcedure {

		public SaveActorArrayProc(DataSource dataSource) {
			super(dataSource, "save_actors");
			declareParameter(new SqlParameter("in_actors", Types.ARRAY, "ACTOR_ARRAY_TYPE"));
		}

		@SuppressWarnings("unchecked")
		public void execute(List<Actor> actors) {
			Map in = Collections.singletonMap("in_actors",
					new SqlStructArrayValue<Actor>(actors.toArray(new Actor[0]), new ActorMapper(), "ACTOR_TYPE"));
			this.execute(in);
		}

	}
}
