package com.yukihira.bookstore;

import com.yukihira.bookstore.order.CheckoutForm;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckoutFormValidationTests {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsValidVietnamesePhoneAndDetailedAddress() {
        CheckoutForm form = validForm();

        assertThat(validator.validate(form)).isEmpty();
    }

    @Test
    void rejectsInvalidPhoneNumber() {
        CheckoutForm form = validForm();
        form.setReceiverPhone("2002029029");

        assertThat(validator.validateProperty(form, "receiverPhone")).isNotEmpty();
    }

    @Test
    void rejectsAddressThatIsTooShortOrContainsInvalidCharacters() {
        CheckoutForm form = validForm();
        form.setShippingAddress("ngách 189");

        assertThat(validator.validateProperty(form, "shippingAddress")).isNotEmpty();

        form.setShippingAddress("Số 2, đường A <script>");
        assertThat(validator.validateProperty(form, "shippingAddress")).isNotEmpty();
    }

    private CheckoutForm validForm() {
        CheckoutForm form = new CheckoutForm();
        form.setReceiverName("Nguyễn Văn An");
        form.setReceiverPhone("0912345678");
        form.setShippingAddress("Số 2, ngách 189, Cầu Giấy, Hà Nội");
        return form;
    }
}
