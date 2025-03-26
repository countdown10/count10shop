package xyz.tomorrowlearncamp.count10shop.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.Const;

@Getter
@RequiredArgsConstructor
public class SignUpUserRequestDto {
	@NotNull
	@Email
	@Size(min = 8, max = 100)
	private final String email;

	@NotNull
	@Pattern(regexp = Const.PASSWORD_REGEX)
	@Size(min = 8, max = 20)
	private final String password;
}
