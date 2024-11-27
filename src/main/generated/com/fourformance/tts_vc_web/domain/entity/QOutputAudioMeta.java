package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutputAudioMeta is a Querydsl query type for OutputAudioMeta
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutputAudioMeta extends EntityPathBase<OutputAudioMeta> {

    private static final long serialVersionUID = -181343679L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutputAudioMeta outputAudioMeta = new QOutputAudioMeta("outputAudioMeta");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.AudioFormat> audioFormat = createEnum("audioFormat", com.fourformance.tts_vc_web.common.constant.AudioFormat.class);

    public final StringPath audioUrl = createString("audioUrl");

    public final StringPath bucketRoute = createString("bucketRoute");

    public final QConcatProject concatProject;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.ProjectType> projectType = createEnum("projectType", com.fourformance.tts_vc_web.common.constant.ProjectType.class);

    public final QTTSDetail ttsDetail;

    public final QVCDetail vcDetail;

    public QOutputAudioMeta(String variable) {
        this(OutputAudioMeta.class, forVariable(variable), INITS);
    }

    public QOutputAudioMeta(Path<? extends OutputAudioMeta> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutputAudioMeta(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutputAudioMeta(PathMetadata metadata, PathInits inits) {
        this(OutputAudioMeta.class, metadata, inits);
    }

    public QOutputAudioMeta(Class<? extends OutputAudioMeta> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concatProject = inits.isInitialized("concatProject") ? new QConcatProject(forProperty("concatProject"), inits.get("concatProject")) : null;
        this.ttsDetail = inits.isInitialized("ttsDetail") ? new QTTSDetail(forProperty("ttsDetail"), inits.get("ttsDetail")) : null;
        this.vcDetail = inits.isInitialized("vcDetail") ? new QVCDetail(forProperty("vcDetail"), inits.get("vcDetail")) : null;
    }

}

