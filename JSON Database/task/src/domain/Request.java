package domain;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request<T, V> {
    @Parameter(names = "-t")
    private String type;

    @Parameter(names = "-k")
    private V key;

    @Parameter(names = "-v")
    private T value;

    @Parameter(names = "-in")
    private String fileName;

    public Request() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public V getKey() {
        return key;
    }

    public void setKey(V key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value =  value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
