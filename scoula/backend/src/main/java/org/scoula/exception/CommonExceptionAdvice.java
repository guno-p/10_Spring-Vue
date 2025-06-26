package org.scoula.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Log4j2
@Order(1)
public class CommonExceptionAdvice {
  // 📍 404 에러 전용 처리
  @ExceptionHandler(NoHandlerFoundException.class)
  public String handle404(NoHandlerFoundException ex) {
    log.error("404 Error: " + ex.getMessage());
    return "/resources/index.html";
    // return 의 동작방식에 대해 이해가 필요.
  }
}
