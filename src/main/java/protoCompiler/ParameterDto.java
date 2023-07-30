package ProtoCompiler.src.main.java.protoCompiler;

import java.lang.reflect.AnnotatedType;

public class ParameterDto {
    private String name;
    private Class<?> type;

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
