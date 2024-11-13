package home.project.service.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Optional;

public class CustomOptionalSerializer extends StdSerializer<Optional<?>> {

    public CustomOptionalSerializer() {
        super(Optional.class, false);
    }

    @Override
    public void serialize(Optional<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value.isPresent()) {
            gen.writeObject(value.get());
        } else {
            gen.writeNull();
        }
    }
}