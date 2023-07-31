// ProtoMessage.java
package ProtoCompiler.src.main.java.protocompiler;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ProtoMessage {
    private String messageName;
    private Parameter[] parameters;
    private String methodName;
    private Class<?> returnType;
    private String returnName;

    private Type genericReturnType;

    public Type getGenericReturnType() {
        return genericReturnType;
    }

    public void setGenericReturnType(Type genericReturnType) {
        this.genericReturnType = genericReturnType;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }
}
