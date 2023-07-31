package ProtoCompiler.src.main.java.protocompiler;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public class ParameterDto {
    private String name;
    private Class<?> type;

    private Type genericReturnType;

    public Type getGenericReturnType() {
        return genericReturnType;
    }

    public void setGenericReturnType(Type genericReturnType) {
        this.genericReturnType = genericReturnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    public void setAnnotatedType(AnnotatedType annotatedType) {
        this.annotatedType = annotatedType;
    }

    private AnnotatedType annotatedType;
}
