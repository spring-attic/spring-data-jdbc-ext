package org.springframework.data.jdbc.query.generated;

import com.mysema.query.sql.PrimaryKey;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.sql.Table;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.BeanPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QCustomer is a Querydsl query type for QCustomer
 */
@Table("customer")
public class QBadCustomer extends RelationalPathBase<QBadCustomer> {

    private static final long serialVersionUID = -409565065;

    public static final QBadCustomer customer = new QBadCustomer("customer");

    public final StringPath firstName = createString("firstname");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastname");

    public final PrimaryKey<QBadCustomer> primary = createPrimaryKey(id);

    public QBadCustomer(String variable) {
        super(QBadCustomer.class, forVariable(variable));
    }

    public QBadCustomer(BeanPath<? extends QBadCustomer> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QBadCustomer(PathMetadata<?> metadata) {
        super(QBadCustomer.class, metadata);
    }

}

