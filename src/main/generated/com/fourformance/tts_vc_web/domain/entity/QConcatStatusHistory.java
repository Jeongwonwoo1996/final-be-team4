package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConcatStatusHistory is a Querydsl query type for ConcatStatusHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcatStatusHistory extends EntityPathBase<ConcatStatusHistory> {

    private static final long serialVersionUID = 2024807765L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConcatStatusHistory concatStatusHistory = new QConcatStatusHistory("concatStatusHistory");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final QConcatProject concatProject;

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.ConcatStatusConst> concatStatusConst = createEnum("concatStatusConst", com.fourformance.tts_vc_web.common.constant.ConcatStatusConst.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final ListPath<String, StringPath> userAudioList = this.<String, StringPath>createList("userAudioList", String.class, StringPath.class, PathInits.DIRECT2);

    public QConcatStatusHistory(String variable) {
        this(ConcatStatusHistory.class, forVariable(variable), INITS);
    }

    public QConcatStatusHistory(Path<? extends ConcatStatusHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConcatStatusHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConcatStatusHistory(PathMetadata metadata, PathInits inits) {
        this(ConcatStatusHistory.class, metadata, inits);
    }

    public QConcatStatusHistory(Class<? extends ConcatStatusHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concatProject = inits.isInitialized("concatProject") ? new QConcatProject(forProperty("concatProject"), inits.get("concatProject")) : null;
    }

}

