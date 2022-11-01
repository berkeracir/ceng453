package group5.backend.user;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserNullAdvice {

    @ResponseBody
    @ExceptionHandler(UserNullException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String userNullHandler(UserNullException ex) {
        return ex.getMessage();
    }
}