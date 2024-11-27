package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberAudioMeta is a Querydsl query type for MemberAudioMeta
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberAudioMeta extends EntityPathBase<MemberAudioMeta> {

    private static final long serialVersionUID = -76335928L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberAudioMeta memberAudioMeta = new QMemberAudioMeta("memberAudioMeta");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.AudioFormat> audioFormat = createEnum("audioFormat", com.fourformance.tts_vc_web.common.constant.AudioFormat.class);

    public final EnumPath<com.fourformance.tts_vc_web.common.constant.AudioType> audioType = createEnum("audioType", com.fourformance.tts_vc_web.common.constant.AudioType.class);

    public final StringPath audioUrl = createString("audioUrl");

    public final StringPath bucketRoute = createString("bucketRoute");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isSelected = createBoolean("isSelected");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMember member;

    public final StringPath script = createString("script");

    public final StringPath trgVoiceId = createString("trgVoiceId");

    public QMemberAudioMeta(String variable) {
        this(MemberAudioMeta.class, forVariable(variable), INITS);
    }

    public QMemberAudioMeta(Path<? extends MemberAudioMeta> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberAudioMeta(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberAudioMeta(PathMetadata metadata, PathInits inits) {
        this(MemberAudioMeta.class, metadata, inits);
    }

    public QMemberAudioMeta(Class<? extends MemberAudioMeta> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

