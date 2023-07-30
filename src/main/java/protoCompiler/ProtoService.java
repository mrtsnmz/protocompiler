// ProtoService.java
package ProtoCompiler.src.main.java.protoCompiler;

import java.util.List;

public class ProtoService {
    private String serviceName;
    private List<ProtoMessage> requestMessages;
    private List<ProtoMessage> responseMessages;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<ProtoMessage> getRequestMessages() {
        return requestMessages;
    }

    public void setRequestMessages(List<ProtoMessage> requestMessages) {
        this.requestMessages = requestMessages;
    }

    public List<ProtoMessage> getResponseMessages() {
        return responseMessages;
    }

    public void setResponseMessages(List<ProtoMessage> responseMessages) {
        this.responseMessages = responseMessages;
    }

    @Override
    public String toString() {
        return "ProtoService{" +
                "serviceName='" + serviceName + '\'' +
                ", requestMessages=" + requestMessages +
                ", responseMessages=" + responseMessages +
                '}';
    }
}
