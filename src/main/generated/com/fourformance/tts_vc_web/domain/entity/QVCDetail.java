package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVCDetail is a Querydsl query type for VCDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVCDetail extends EntityPathBase<VCDetail> {

    private static final long serialVersionUID = -1028014121L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVCDetail vCDetail = new QVCDetail("vCDetail");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final ListPath<APIStatus, QAPIStatus> apiStatuses = this.<APIStatus, QAPIStatus>createList("apiStatuses", APIStatus.class, QAPIStatus.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isChecked = createBoolean("isChecked");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMemberAudioMeta memberAudioMeta;

    public final StringPath unitScript = createString("unitScript");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QVCProject vcProject;

    public QVCDetail(String variable) {
        this(VCDetail.class, forVariable(variable), INITS);
    }

    public QVCDetail(Path<? extends VCDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVCDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVCDetail(PathMetadata metadata, PathInits inits) {
        this(VCDetail.class, metadata, inits);
    }

    public QVCDetail(Class<? extends VCDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberAudioMeta = inits.isInitialized("memberAudioMeta") ? new QMemberAudioMeta(forProperty("memberAudioMeta"), inits.get("memberAudioMeta")) : null;
        this.vcProject = inits.isInitialized("vcProject") ? new QVCProject(forProperty("vcProject"), inits.get("vcProject")) : null;
    }

}

