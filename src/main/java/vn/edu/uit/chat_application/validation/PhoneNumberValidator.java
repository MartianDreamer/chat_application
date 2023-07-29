package vn.edu.uit.chat_application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null ||
                (value.length() >= 10
                        && value.length() <= 13
                        && (value.matches("[0-9]{10,}")
                        || value.matches("/+[0-9]{11}")));
    }
}
