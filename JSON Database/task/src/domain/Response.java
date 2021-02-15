package domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String response;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T value;

    public Response(String response, String reason, T value) {
        this.response = response;
        this.reason = reason;
        this.value = value;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static class ResponseBuilder<T>{
        private String response;
        private String reason;
        private T value;


        public ResponseBuilder<T> response(String response){
            this.response = response;
            return this;
        }

        public ResponseBuilder<T> reason(String reason){
            this.reason = reason;
            return this;
        }

        public ResponseBuilder<T> value(T value){
            this.value = value;
            return this;
        }


        public Response<T> build(){
            return new Response<T>(response,reason,value);
        }
    }
}


