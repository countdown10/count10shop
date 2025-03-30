package xyz.tomorrowlearncamp.count10shop.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.Const;

@Getter
@AllArgsConstructor
public class UpdateUserRequestDto {
	@Email
	@Size(min = 10, max = 30)
	private final String email;

	@NotNull
	@Size(min = 8, max = 20)
	@Pattern(regexp = Const.PASSWORD_REGEX)
	private final String password;

	@Size(min = 8, max = 100)
	private final String address;

}
