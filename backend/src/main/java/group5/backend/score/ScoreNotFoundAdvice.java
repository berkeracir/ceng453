package group5.backend.score;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ScoreNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(ScoreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String scoreNotFoundHandler(ScoreNotFoundException ex) {
        return ex.getMessage();
    }
}
