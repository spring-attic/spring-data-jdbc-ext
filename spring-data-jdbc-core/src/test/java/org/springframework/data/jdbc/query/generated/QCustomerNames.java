package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QCustomerNames is a Querydsl query type for QCustomerNames
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCustomerNames extends com.mysema.query.sql.RelationalPathBase<QCustomerNames> {

    private static final long serialVersionUID = 1221071121;

    public static final QCustomerNames customerNames = new QCustomerNames("CUSTOMER_NAMES");

    public final StringPath name = createString("NAME");

    public QCustomerNames(String variable) {
        super(QCustomerNames.class, forVariable(variable), "PUBLIC", "CUSTOMER_NAMES");
    }

    public QCustomerNames(Path<? extends QCustomerNames> entity) {
        super(entity.getType(), entity.getMetadata(), "PUBLIC", "CUSTOMER_NAMES");
    }

    public QCustomerNames(PathMetadata<?> metadata) {
        super(QCustomerNames.class, metadata, "PUBLIC", "CUSTOMER_NAMES");
    }

}

