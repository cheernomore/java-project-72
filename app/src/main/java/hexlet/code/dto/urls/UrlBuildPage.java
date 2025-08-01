package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlBuildPage extends BasePage {
    private String name;
    private Map<String, List<ValidationError<Object>>> errors;
}
