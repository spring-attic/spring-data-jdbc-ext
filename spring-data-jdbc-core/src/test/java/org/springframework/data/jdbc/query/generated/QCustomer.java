package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QCustomer is a Querydsl query type for QCustomer
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCustomer extends com.mysema.query.sql.RelationalPathBase<QCustomer> {

    private static final long serialVersionUID = 676296244;

    public static final QCustomer customer = new QCustomer("CUSTOMER");

    public final StringPath firstName = createString("firstName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastName");

    public final com.mysema.query.sql.PrimaryKey<QCustomer> sysPk10025 = createPrimaryKey(id);

    public QCustomer(String variable) {
        super(QCustomer.class,  forVariable(variable), "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public QCustomer(Path<? extends QCustomer> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public QCustomer(PathMetadata<?> metadata) {
        super(QCustomer.class,  metadata, "PUBLIC", "CUSTOMER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(firstName, ColumnMetadata.named("FIRST_NAME").ofType(12).withSize(255));
        addMetadata(id, ColumnMetadata.named("ID").ofType(-5).withSize(0).notNull());
        addMetadata(lastName, ColumnMetadata.named("LAST_NAME").ofType(12).withSize(255));
    }

}

