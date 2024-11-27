package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConcatDetail is a Querydsl query type for ConcatDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcatDetail extends EntityPathBase<ConcatDetail> {

    private static final long serialVersionUID = 910914206L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConcatDetail concatDetail = new QConcatDetail("concatDetail");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final NumberPath<Integer> audioSeq = createNumber("audioSeq", Integer.class);

    public final QConcatProject concatProject;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Float> endSilence = createNumber("endSilence", Float.class);

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

    public QConcatDetail(String variable) {
        this(ConcatDetail.class, forVariable(variable), INITS);
    }

    public QConcatDetail(Path<? extends ConcatDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConcatDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConcatDetail(PathMetadata metadata, PathInits inits) {
        this(ConcatDetail.class, metadata, inits);
    }

    public QConcatDetail(Class<? extends ConcatDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concatProject = inits.isInitialized("concatProject") ? new QConcatProject(forProperty("concatProject"), inits.get("concatProject")) : null;
        this.memberAudioMeta = inits.isInitialized("memberAudioMeta") ? new QMemberAudioMeta(forProperty("memberAudioMeta"), inits.get("memberAudioMeta")) : null;
    }

}

