package templates;

import headers.ResponseCode;

/**
 * Created by vadim on 22.08.15.
 */
public class TemplateGenerator {
    private TemplateGenerator() {
        // unused
    }

    public static byte[] generate(ResponseCode responseCode) {
        StringBuilder buf = new StringBuilder(128);
        buf.append("<html>\r\n")
           .append("<head>\r\n")
           .append("<title>").append(responseCode.getCode())
                .append(" ").append(responseCode.getCodeTitle())
                .append("</title>\r\n")
           .append("</head>\r\n")
           .append("<body bgcolor=\"white\">\r\n")
           .append("<center><h1>").append(responseCode.getCode())
                .append(" ").append(responseCode.getCodeTitle()).append("</h1></center>\r\n")
           .append("<hr><center>LarionovServer</center>\r\n")
           .append("</body>\r\n</html>\r\n");

        return String.valueOf(buf).getBytes();
    }
}
