package io.crnk.jpa.meta.internal;

import java.lang.reflect.Type;
import javax.persistence.Embeddable;

import io.crnk.core.engine.information.bean.BeanAttributeInformation;
import io.crnk.core.engine.internal.utils.ClassUtils;
import io.crnk.jpa.annotations.BlobBasedEmbeddable;
import io.crnk.jpa.meta.MetaEmbeddable;
import io.crnk.jpa.meta.MetaEmbeddableAttribute;
import io.crnk.jpa.query.AnyTypeObject;
import io.crnk.meta.model.MetaAttribute;
import io.crnk.meta.model.MetaDataObject;
import io.crnk.meta.model.MetaElement;

public class EmbeddableMetaFactory extends AbstractJpaDataObjectFactory<MetaEmbeddable> {

	private static final Object VALUE_ANYTYPE_ATTR_NAME = "value";

	@Override
	public boolean accept(Type type) {
		Class<?> rawType = ClassUtils.getRawType(type);
		return rawType.getAnnotation(Embeddable.class) != null || rawType.getAnnotation(BlobBasedEmbeddable.class) != null;
	}

	@Override
	public MetaEmbeddable create(Type type) {
		Class<?> rawClazz = ClassUtils.getRawType(type);
		Class<?> superClazz = rawClazz.getSuperclass();
		MetaElement superMeta = null;
		if (superClazz != Object.class) {
			superMeta = context.allocate(superClazz);
		}
		MetaEmbeddable meta = new MetaEmbeddable();
		meta.setElementType(meta);
		meta.setName(rawClazz.getSimpleName());
		meta.setImplementationType(type);
		meta.setSuperType((MetaDataObject) superMeta);
		if (superMeta != null) {
			((MetaDataObject) superMeta).addSubType(meta);
		}
		createAttributes(meta);
		return meta;
	}

	@Override
	protected String getMetaName(BeanAttributeInformation attrInformation) {
		return attrInformation.getName();
	}

	@Override
	protected MetaAttribute createAttribute(MetaEmbeddable metaDataObject, String name) {
		MetaEmbeddableAttribute attr = new MetaEmbeddableAttribute();
		attr.setParent(metaDataObject, true);
		attr.setName(name);
		attr.setFilterable(true);
		attr.setSortable(true);

		if (AnyTypeObject.class.isAssignableFrom(metaDataObject.getImplementationClass()) && name
				.equals(VALUE_ANYTYPE_ATTR_NAME)) {
			attr.setDerived(true);
		}

		return attr;
	}
}