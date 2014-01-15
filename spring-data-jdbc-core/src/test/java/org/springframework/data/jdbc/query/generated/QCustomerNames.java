package org.springframework.data.jdbc.query.generated;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QCustomerNames is a Querydsl query type for QCustomerNames
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCustomerNames extends com.mysema.query.sql.RelationalPathBase<QCustomerNames> {

    private static final long serialVersionUID = 1780429172;

    public static final QCustomerNames customerNames = new QCustomerNames("CUSTOMER_NAMES");

    public final StringPath name = createString("name");

    public QCustomerNames(String variable) {
        super(QCustomerNames.class,  forVariable(variable), "PUBLIC", "CUSTOMER_NAMES");
        addMetadata();
    }

    public QCustomerNames(Path<? extends QCustomerNames> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CUSTOMER_NAMES");
        addMetadata();
    }

    public QCustomerNames(PathMetadata<?> metadata) {
        super(QCustomerNames.class,  metadata, "PUBLIC", "CUSTOMER_NAMES");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").ofType(12).withSize(255));
    }

}

