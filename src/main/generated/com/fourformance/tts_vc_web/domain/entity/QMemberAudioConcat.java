package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberAudioConcat is a Querydsl query type for MemberAudioConcat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberAudioConcat extends EntityPathBase<MemberAudioConcat> {

    private static final long serialVersionUID = -621612777L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberAudioConcat memberAudioConcat = new QMemberAudioConcat("memberAudioConcat");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final QConcatProject concatProject;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMemberAudioMeta memberAudioMeta;

    public QMemberAudioConcat(String variable) {
        this(MemberAudioConcat.class, forVariable(variable), INITS);
    }

    public QMemberAudioConcat(Path<? extends MemberAudioConcat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberAudioConcat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberAudioConcat(PathMetadata metadata, PathInits inits) {
        this(MemberAudioConcat.class, metadata, inits);
    }

    public QMemberAudioConcat(Class<? extends MemberAudioConcat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concatProject = inits.isInitialized("concatProject") ? new QConcatProject(forProperty("concatProject"), inits.get("concatProject")) : null;
        this.memberAudioMeta = inits.isInitialized("memberAudioMeta") ? new QMemberAudioMeta(forProperty("memberAudioMeta"), inits.get("memberAudioMeta")) : null;
    }

}

