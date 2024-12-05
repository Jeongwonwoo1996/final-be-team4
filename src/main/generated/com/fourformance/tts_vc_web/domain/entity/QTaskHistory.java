package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTaskHistory is a Querydsl query type for TaskHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTaskHistory extends EntityPathBase<TaskHistory> {

    private static final long serialVersionUID = -2094286314L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaskHistory taskHistory = new QTaskHistory("taskHistory");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> created_at = createDateTime("created_at", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final NumberPath<Long> lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath mod_msg = createString("mod_msg");

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.TaskStatusConst> newStatus = createEnum("newStatus", com.fourformance.tts_vc_web.common.constant.TaskStatusConst.class);

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.TaskStatusConst> oldStatus = createEnum("oldStatus", com.fourformance.tts_vc_web.common.constant.TaskStatusConst.class);

    public final QTask task;

    public QTaskHistory(String variable) {
        this(TaskHistory.class, forVariable(variable), INITS);
    }

    public QTaskHistory(Path<? extends TaskHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTaskHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTaskHistory(PathMetadata metadata, PathInits inits) {
        this(TaskHistory.class, metadata, inits);
    }

    public QTaskHistory(Class<? extends TaskHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
    }

}

