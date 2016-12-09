package org.netbeans.asciidoc;

import java.util.Arrays;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;

public final class AsciidoctorConverter {
    private static final LazyValue<AsciidoctorConverter> DEFAULT_REF = new LazyValue<>(AsciidoctorConverter::new);

    private final Asciidoctor doctor;

    private AsciidoctorConverter() {
        this.doctor = Asciidoctor.Factory.create(Arrays.asList(
                "gems/asciidoctor-1.5.4/lib",
                "gems/coderay-1.1.0/lib",
                "META-INF/jruby.home/lib/ruby/2.0"));
    }

    public static AsciidoctorConverter getDefault() {
        return DEFAULT_REF.get();
    }

    public String convert(String src, Options options) {
        return doctor.convert(src, options);
    }
}
