package xyz.tomorrowlearncamp.count10shop.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.Const;

@Getter
@AllArgsConstructor
public class DeleteUserRequestDto {
	@NotNull
	@Size(min = 8, max = 20)
	@Pattern(regexp = Const.PASSWORD_REGEX)
	private final String password;
}
