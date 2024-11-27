package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberAudioVC is a Querydsl query type for MemberAudioVC
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberAudioVC extends EntityPathBase<MemberAudioVC> {

    private static final long serialVersionUID = 187630096L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberAudioVC memberAudioVC = new QMemberAudioVC("memberAudioVC");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

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

    public final QVCProject vcProject;

    public QMemberAudioVC(String variable) {
        this(MemberAudioVC.class, forVariable(variable), INITS);
    }

    public QMemberAudioVC(Path<? extends MemberAudioVC> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberAudioVC(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberAudioVC(PathMetadata metadata, PathInits inits) {
        this(MemberAudioVC.class, metadata, inits);
    }

    public QMemberAudioVC(Class<? extends MemberAudioVC> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberAudioMeta = inits.isInitialized("memberAudioMeta") ? new QMemberAudioMeta(forProperty("memberAudioMeta"), inits.get("memberAudioMeta")) : null;
        this.vcProject = inits.isInitialized("vcProject") ? new QVCProject(forProperty("vcProject"), inits.get("vcProject")) : null;
    }

}

