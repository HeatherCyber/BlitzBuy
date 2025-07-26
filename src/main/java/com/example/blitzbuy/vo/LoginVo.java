package com.example.blitzbuy.vo;

import com.example.blitzbuy.validator.IsMobile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Heather
 * @version 1.0
 * Data transfer object (DTO) for encapsulating user login credentials.
 * This class serves as the request body for authentication endpoints,
 * validating input constraints before processing. All fields are mandatory
 * and subject to format validation.
 */
@Data
public class LoginVo {

    /**
     * User's mobile number in standard format.
     * Validation Rules:
     * -Must not be null (enforced by {@link NotNull})
     * -Must match standard mobile pattern (enforced by {@link IsMobile})
     * Example:"+8613812345678"
     */
    @NotNull
    @IsMobile
    private String mobile;

    /**
     * MD5 password string.
     * Security Requirements:
     * -Must not be null (enforced by {@link NotNull})
     * -Minimum 32 characters (enforced by {@link Length})
     * -Should be pre-hashed (e.g., MD5(input password + salt)) before transmission
     */
    @NotNull
    @Length(min = 32)
    private String password;

}
