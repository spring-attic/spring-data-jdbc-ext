package org.springframework.data.jdbc.test.adt;

import java.util.List;

/**
 * @author trisberg
 */
public interface AdvancedDataTypesDao {

    void addActor(Actor actor);

    Actor getActor(Long id);

    void addSqlActor(SqlActor actor);

    SqlActor getSqlActor(Long id);

    String[] getActorNames();

    void deleteActors(Long[] ids);

    List<Actor> getActors();

	List<Actor> getAllActors();

	void saveActors(List<Actor> actors);
}
