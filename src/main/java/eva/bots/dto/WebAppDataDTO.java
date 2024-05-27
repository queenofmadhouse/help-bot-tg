package eva.bots.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class WebAppDataDTO {

    String userName;
    String userPronouns;
    String userRequest;
}
