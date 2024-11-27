package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTTSDetail is a Querydsl query type for TTSDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTTSDetail extends EntityPathBase<TTSDetail> {

    private static final long serialVersionUID = 843843755L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTTSDetail tTSDetail = new QTTSDetail("tTSDetail");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final ListPath<APIStatus, QAPIStatus> apiStatuses = this.<APIStatus, QAPIStatus>createList("apiStatuses", APIStatus.class, QAPIStatus.class, PathInits.DIRECT2);

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

    public final QTTSProject ttsProject;

    public final NumberPath<Float> unitPitch = createNumber("unitPitch", Float.class);

    public final StringPath unitScript = createString("unitScript");

    public final NumberPath<Integer> unitSequence = createNumber("unitSequence", Integer.class);

    public final NumberPath<Float> unitSpeed = createNumber("unitSpeed", Float.class);

    public final NumberPath<Float> unitVolume = createNumber("unitVolume", Float.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QVoiceStyle voiceStyle;

    public QTTSDetail(String variable) {
        this(TTSDetail.class, forVariable(variable), INITS);
    }

    public QTTSDetail(Path<? extends TTSDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTTSDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTTSDetail(PathMetadata metadata, PathInits inits) {
        this(TTSDetail.class, metadata, inits);
    }

    public QTTSDetail(Class<? extends TTSDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ttsProject = inits.isInitialized("ttsProject") ? new QTTSProject(forProperty("ttsProject"), inits.get("ttsProject")) : null;
        this.voiceStyle = inits.isInitialized("voiceStyle") ? new QVoiceStyle(forProperty("voiceStyle")) : null;
    }

}

