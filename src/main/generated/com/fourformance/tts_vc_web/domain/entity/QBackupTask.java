package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBackupTask is a Querydsl query type for BackupTask
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBackupTask extends EntityPathBase<BackupTask> {

    private static final long serialVersionUID = 431601344L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBackupTask backupTask = new QBackupTask("backupTask");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final StringPath backupData = createString("backupData");

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.BackupType> backupType = createEnum("backupType", com.fourformance.tts_vc_web.common.constant.BackupType.class);

    public final DateTimePath<java.time.LocalDateTime> created_at = createDateTime("created_at", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QTask task;

    public QBackupTask(String variable) {
        this(BackupTask.class, forVariable(variable), INITS);
    }

    public QBackupTask(Path<? extends BackupTask> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBackupTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBackupTask(PathMetadata metadata, PathInits inits) {
        this(BackupTask.class, metadata, inits);
    }

    public QBackupTask(Class<? extends BackupTask> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
    }

}

