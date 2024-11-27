package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVoiceStyle is a Querydsl query type for VoiceStyle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoiceStyle extends EntityPathBase<VoiceStyle> {

    private static final long serialVersionUID = -160267272L;

    public static final QVoiceStyle voiceStyle = new QVoiceStyle("voiceStyle");

    public final com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity _super = new com.fourformance.tts_vc_web.domain.baseEntity.QBaseEntity(this);

    public final StringPath country = createString("country");

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isVisible = createBoolean("isVisible");

    public final StringPath languageCode = createString("languageCode");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath personality = createString("personality");

    public final StringPath voiceName = createString("voiceName");

    public final StringPath voiceType = createString("voiceType");

    public QVoiceStyle(String variable) {
        super(VoiceStyle.class, forVariable(variable));
    }

    public QVoiceStyle(Path<? extends VoiceStyle> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVoiceStyle(PathMetadata metadata) {
        super(VoiceStyle.class, metadata);
    }

}

