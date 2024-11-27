package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVCProject is a Querydsl query type for VCProject
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVCProject extends EntityPathBase<VCProject> {

    private static final long serialVersionUID = 624268371L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVCProject vCProject = new QVCProject("vCProject");

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

    public final QMemberAudioMeta memberTargetAudioMeta;

    //inherited
    public final StringPath projectName;

    public final StringPath projectType = createString("projectType");

    public final StringPath trgVoiceId = createString("trgVoiceId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    public QVCProject(String variable) {
        this(VCProject.class, forVariable(variable), INITS);
    }

    public QVCProject(Path<? extends VCProject> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVCProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVCProject(PathMetadata metadata, PathInits inits) {
        this(VCProject.class, metadata, inits);
    }

    public QVCProject(Class<? extends VCProject> type, PathMetadata metadata, PathInits inits) {
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
        this.memberTargetAudioMeta = inits.isInitialized("memberTargetAudioMeta") ? new QMemberAudioMeta(forProperty("memberTargetAudioMeta"), inits.get("memberTargetAudioMeta")) : null;
        this.projectName = _super.projectName;
        this.updatedAt = _super.updatedAt;
    }

}

