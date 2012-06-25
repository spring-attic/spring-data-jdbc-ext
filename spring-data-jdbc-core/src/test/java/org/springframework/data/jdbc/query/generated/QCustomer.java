package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QCustomer is a Querydsl query type for QCustomer
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCustomer extends com.mysema.query.sql.RelationalPathBase<QCustomer> {

    private static final long serialVersionUID = -409565065;

    public static final QCustomer customer = new QCustomer("CUSTOMER");

    public final StringPath firstName = createString("FIRST_NAME");

    public final NumberPath<Long> id = createNumber("ID", Long.class);

    public final StringPath lastName = createString("LAST_NAME");

    public final com.mysema.query.sql.PrimaryKey<QCustomer> primary = createPrimaryKey(id);

    public QCustomer(String variable) {
        super(QCustomer.class, forVariable(variable), "PUBLIC", "CUSTOMER");
    }

    public QCustomer(Path<? extends QCustomer> entity) {
        super(entity.getType(), entity.getMetadata(), "PUBLIC", "CUSTOMER");
    }

    public QCustomer(PathMetadata<?> metadata) {
        super(QCustomer.class, metadata, "PUBLIC", "CUSTOMER");
    }

}

