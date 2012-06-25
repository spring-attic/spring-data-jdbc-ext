package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QCustomer is a Querydsl query type for QCustomer
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBadCustomer extends com.mysema.query.sql.RelationalPathBase<QBadCustomer> {

    private static final long serialVersionUID = -409565065;

    public static final QBadCustomer customer = new QBadCustomer("CUSTOMER");

    public final StringPath firstName = createString("FIRSTNAME");

    public final NumberPath<Long> id = createNumber("ID", Long.class);

    public final StringPath lastName = createString("LASTNAME");

    public final com.mysema.query.sql.PrimaryKey<QBadCustomer> primary = createPrimaryKey(id);

    public QBadCustomer(String variable) {
        super(QBadCustomer.class, forVariable(variable), "PUBLIC", "CUSTOMER");
    }

    public QBadCustomer(Path<? extends QBadCustomer> entity) {
        super(entity.getType(), entity.getMetadata(), "PUBLIC", "CUSTOMER");
    }

    public QBadCustomer(PathMetadata<?> metadata) {
        super(QBadCustomer.class, metadata, "PUBLIC", "CUSTOMER");
    }

}

