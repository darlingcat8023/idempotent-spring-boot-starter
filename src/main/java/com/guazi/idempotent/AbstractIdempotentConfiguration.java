package com.guazi.idempotent;

import com.guazi.idempotent.config.EnableIdempotent;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * @author liujiajun
 * @date 2020/5/27
 */
public class AbstractIdempotentConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableIdempotent;

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        this.enableIdempotent = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableIdempotent.class.getName(), false));
        if (this.enableIdempotent == null) {
            throw new IllegalArgumentException("@EnableIdempotent is not present on importing class " + annotationMetadata.getClassName());
        }
    }

}
