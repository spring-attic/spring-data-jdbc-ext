package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import com.mysema.query.sql.*;
import java.util.*;


/**
 * QCustomerNames is a Querydsl query type for QCustomerNames
 */
@Table("customer_names")
public class QCustomerNames extends RelationalPathBase<QCustomerNames> {

    private static final long serialVersionUID = 1221071121;

    public static final QCustomerNames customerNames = new QCustomerNames("customer_names");

    public final StringPath name = createString("name");

    public QCustomerNames(String variable) {
        super(QCustomerNames.class, forVariable(variable));
    }

    public QCustomerNames(BeanPath<? extends QCustomerNames> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QCustomerNames(PathMetadata<?> metadata) {
        super(QCustomerNames.class, metadata);
    }

}

