package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import com.mysema.query.sql.*;
import java.util.*;


/**
 * QCustomer is a Querydsl query type for QCustomer
 */
@Table("customer")
public class QCustomer extends RelationalPathBase<QCustomer> {

    private static final long serialVersionUID = -409565065;

    public static final QCustomer customer = new QCustomer("customer");

    public final StringPath firstName = createString("first_name");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("last_name");

    public final PrimaryKey<QCustomer> primary = createPrimaryKey(id);

    public QCustomer(String variable) {
        super(QCustomer.class, forVariable(variable));
    }

    public QCustomer(BeanPath<? extends QCustomer> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QCustomer(PathMetadata<?> metadata) {
        super(QCustomer.class, metadata);
    }

}

