package group5.backend.score;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WrongScoreEntryAdvice {

    @ResponseBody
    @ExceptionHandler(WrongScoreEntryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String scoreNotFoundHandler(WrongScoreEntryException ex) {
        return ex.getMessage();
    }
}