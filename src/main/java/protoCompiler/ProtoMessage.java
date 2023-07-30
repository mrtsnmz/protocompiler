// ProtoMessage.java
package ProtoCompiler.src.main.java.protoCompiler;

import java.lang.reflect.Parameter;

public class ProtoMessage {
    private String messageName;
    private Parameter[] parameters;
    private String methodName;
    private Class<?> returnType;
    private String returnName;

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

    @Override
    public String toString() {
        return "ProtoMessage{" +
                "messageName='" + messageName + '\'' +
                ", parameters='" + parameters + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType='" + returnType + '\'' +
                ", returnType='" + returnName + '\'' +
                '}';
    }
}
