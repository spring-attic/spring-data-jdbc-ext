package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QBadCustomer is a Querydsl query type for QBadCustomer
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBadCustomer extends com.mysema.query.sql.RelationalPathBase<QBadCustomer> {

    private static final long serialVersionUID = 676296244;

    public static final QBadCustomer customer = new QBadCustomer("CUSTOMER");

    public final StringPath firstName = createString("firstName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastName");

    public final com.mysema.query.sql.PrimaryKey<QBadCustomer> sysPk10025 = createPrimaryKey(id);

    public QBadCustomer(String variable) {
        super(QBadCustomer.class,  forVariable(variable), "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public QBadCustomer(Path<? extends QBadCustomer> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public QBadCustomer(PathMetadata<?> metadata) {
        super(QBadCustomer.class,  metadata, "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(firstName, ColumnMetadata.named("FIRSTNAME").ofType(12).withSize(255));
        addMetadata(id, ColumnMetadata.named("ID").ofType(-5).withSize(0).notNull());
        addMetadata(lastName, ColumnMetadata.named("LASTNAME").ofType(12).withSize(255));
    }

}

