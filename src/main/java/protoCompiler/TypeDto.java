package ProtoCompiler.src.main.java.protocompiler;

public class TypeDto {
    private String name;
    private Boolean customFlag;

    public Boolean getDecimalFlag() {
        return decimalFlag;
    }

    public void setDecimalFlag(Boolean decimalFlag) {
        this.decimalFlag = decimalFlag;
    }

    private Boolean decimalFlag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCustomFlag() {
        return customFlag;
    }

    public void setCustomFlag(Boolean customFlag) {
        this.customFlag = customFlag;
    }
}
