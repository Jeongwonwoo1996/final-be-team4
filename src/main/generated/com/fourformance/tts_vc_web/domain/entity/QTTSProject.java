package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTTSProject is a Querydsl query type for TTSProject
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTTSProject extends EntityPathBase<TTSProject> {

    private static final long serialVersionUID = -1477679617L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTTSProject tTSProject = new QTTSProject("tTSProject");

    public final QProject _super;

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.APIStatusConst> apiStatus = createEnum("apiStatus", com.fourformance.tts_vc_web.common.constant.APIStatusConst.class);

    public final DateTimePath<java.time.LocalDateTime> APIStatusModifiedAt = createDateTime("APIStatusModifiedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final StringPath createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    public final StringPath fullScript = createString("fullScript");

    public final NumberPath<Float> globalPitch = createNumber("globalPitch", Float.class);

    public final NumberPath<Float> globalSpeed = createNumber("globalSpeed", Float.class);

    public final NumberPath<Float> globalVolume = createNumber("globalVolume", Float.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final BooleanPath isDeleted;

    //inherited
    public final StringPath lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate;

    // inherited
    public final QMember member;

    //inherited
    public final StringPath projectName;

    public final StringPath projectType = createString("projectType");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public final QVoiceStyle voiceStyle;

    public QTTSProject(String variable) {
        this(TTSProject.class, forVariable(variable), INITS);
    }

    public QTTSProject(Path<? extends TTSProject> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTTSProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTTSProject(PathMetadata metadata, PathInits inits) {
        this(TTSProject.class, metadata, inits);
    }

    public QTTSProject(Class<? extends TTSProject> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QProject(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.deletedAt = _super.deletedAt;
        this.id = _super.id;
        this.isDeleted = _super.isDeleted;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.member = _super.member;
        this.projectName = _super.projectName;
        this.updatedAt = _super.updatedAt;
        this.voiceStyle = inits.isInitialized("voiceStyle") ? new QVoiceStyle(forProperty("voiceStyle")) : null;
    }

}

