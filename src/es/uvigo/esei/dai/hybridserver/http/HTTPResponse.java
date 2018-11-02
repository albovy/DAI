package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
    private HTTPResponseStatus status;
    private String version;
    private String content;
    private Map<String, String> parameters;

    public HTTPResponse() {
        this.status = null;
        this.version = "";
        this.content = null;
        this.parameters = new LinkedHashMap<>();
    }

    public HTTPResponseStatus getStatus() {
        return this.status;
    }

    public void setStatus(HTTPResponseStatus status) {
        this.status = status;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public String putParameter(String name, String value) {
        return parameters.put(name, value);
    }

    public boolean containsParameter(String name) {
        return parameters.containsKey(name);
    }

    public String removeParameter(String name) {
        return parameters.remove(name);
    }

    public void clearParameters() {
        parameters.clear();
    }

    public List<String> listParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    public void print(Writer writer) throws IOException {
        writer.append(this.getVersion()).append(" ");

        writer.append(Integer.toString(this.getStatus().getCode())).append(" ").append(this.getStatus().getStatus());

        if (content != null)
            writer.append("\r\nContent-Length: ").append(String.valueOf(this.getContent().length()));

        if (this.parameters.get("Content-Language") != null)
            writer.append("\r\nContent-Language: ").append(this.parameters.get("Content-Language"));

        if (this.parameters.get("Content-Type") != null)
            writer.append("\r\nContent-Type: ").append(this.parameters.get("Content-Type"));

        if (this.parameters.get("Content-Encoding") != null)
            writer.append("\r\nContent-Encoding: ").append(this.parameters.get("Content-Encoding"));

        writer.append("\r\n\r\n");

        if (content != null) writer.append(this.getContent());

    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();

        try {
            this.print(writer);
        } catch (IOException e) {
            System.out.println("Error " + e);
        }

        return writer.toString();
    }
}
