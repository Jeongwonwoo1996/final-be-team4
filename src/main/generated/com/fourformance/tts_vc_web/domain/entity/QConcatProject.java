package com.fourformance.tts_vc_web.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConcatProject is a Querydsl query type for ConcatProject
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcatProject extends EntityPathBase<ConcatProject> {

    private static final long serialVersionUID = 601504364L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConcatProject concatProject = new QConcatProject("concatProject");

    public final QProject _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final StringPath createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt;

    public final NumberPath<Float> globalFrontSilenceLength = createNumber("globalFrontSilenceLength", Float.class);

    public final NumberPath<Float> globalTotalSilenceLength = createNumber("globalTotalSilenceLength", Float.class);

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

    public QConcatProject(String variable) {
        this(ConcatProject.class, forVariable(variable), INITS);
    }

    public QConcatProject(Path<? extends ConcatProject> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConcatProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConcatProject(PathMetadata metadata, PathInits inits) {
        this(ConcatProject.class, metadata, inits);
    }

    public QConcatProject(Class<? extends ConcatProject> type, PathMetadata metadata, PathInits inits) {
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
    }

}

