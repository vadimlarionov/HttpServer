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
        buf.append("<html><body><h3>");
        buf.append(responseCode.getCode());
        buf.append(". ").append(responseCode.getCodeTitle());
        buf.append("</h3></body></html>");
        return String.valueOf(buf).getBytes();
    }
}
