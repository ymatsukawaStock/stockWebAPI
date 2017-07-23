package jp.ymatsukawa.stockapi.tool.communication;

import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

public class RequestValidator {
  private RequestValidator() {}
  private static class RequestValidatorPreservation {
    private static final RequestValidator INSTANCE = new RequestValidator();
  }

  // return set of error
  public static Set getErrors(BindingResult bindingResult) {
    Set<String> errors = new HashSet<>();
    if(bindingResult.hasErrors()) {
      bindingResult.getAllErrors().forEach(error -> {
        errors.add(error.getDefaultMessage());
      });
    }
    return errors;
  }

  public static <T> Set getErrors(T bean) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    // validate bean
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);
    // set bean error
    Set<String> beanErrors = new HashSet<>();
    constraintViolations.forEach(constraintViolation -> {
      beanErrors.add(constraintViolation.getMessage());
    });

    return beanErrors;
  }

}
