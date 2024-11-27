package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAPIStatus is a Querydsl query type for APIStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAPIStatus extends EntityPathBase<APIStatus> {

    private static final long serialVersionUID = -1692893389L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAPIStatus aPIStatus = new QAPIStatus("aPIStatus");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst> apiUnitStatusConst = createEnum("apiUnitStatusConst", com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final DateTimePath<java.time.LocalDateTime> requestAt = createDateTime("requestAt", java.time.LocalDateTime.class);

    public final StringPath requestPayload = createString("requestPayload");

    public final DateTimePath<java.time.LocalDateTime> responseAt = createDateTime("responseAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> responseCode = createNumber("responseCode", Integer.class);

    public final StringPath responsePayload = createString("responsePayload");

    public final QTTSDetail ttsDetail;

    public final QVCDetail vcDetail;

    public QAPIStatus(String variable) {
        this(APIStatus.class, forVariable(variable), INITS);
    }

    public QAPIStatus(Path<? extends APIStatus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAPIStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAPIStatus(PathMetadata metadata, PathInits inits) {
        this(APIStatus.class, metadata, inits);
    }

    public QAPIStatus(Class<? extends APIStatus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ttsDetail = inits.isInitialized("ttsDetail") ? new QTTSDetail(forProperty("ttsDetail"), inits.get("ttsDetail")) : null;
        this.vcDetail = inits.isInitialized("vcDetail") ? new QVCDetail(forProperty("vcDetail"), inits.get("vcDetail")) : null;
    }

}

