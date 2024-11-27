package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMultiJobLog is a Querydsl query type for MultiJobLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMultiJobLog extends EntityPathBase<MultiJobLog> {

    private static final long serialVersionUID = -1137078361L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMultiJobLog multiJobLog = new QMultiJobLog("multiJobLog");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> endedAt = createDateTime("endedAt", java.time.LocalDateTime.class);

    public final StringPath failBy = createString("failBy");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.MultiJobLogStatusConst> multiJobLogStatusConst = createEnum("multiJobLogStatusConst", com.fourformance.tts_vc_web.common.constant.MultiJobLogStatusConst.class);

    public final QProject project;

    public final StringPath projectName = createString("projectName");

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.ProjectType> projectType = createEnum("projectType", com.fourformance.tts_vc_web.common.constant.ProjectType.class);

    public final NumberPath<Integer> sequence = createNumber("sequence", Integer.class);

    public QMultiJobLog(String variable) {
        this(MultiJobLog.class, forVariable(variable), INITS);
    }

    public QMultiJobLog(Path<? extends MultiJobLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMultiJobLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMultiJobLog(PathMetadata metadata, PathInits inits) {
        this(MultiJobLog.class, metadata, inits);
    }

    public QMultiJobLog(Class<? extends MultiJobLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

