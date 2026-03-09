package com.viettelpost.fms.utm_integration.exception;

import com.viettelpost.fms.common.dto.BaseErrorDto;
import com.viettelpost.fms.common.i18n.I18nMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
@Slf4j
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private I18nMessageService i18nMessageService;

    /**
     * Handle the Access Denied Exception
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("", ex);
        String msg = i18nMessageService.translate("error.permission");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
    }

    @ExceptionHandler({
            InternalException.class
    })
    public ResponseEntity<Object> handleInternalException(InternalException ex) {
        String msg = i18nMessageService.translate(ex.getMessage(), LocaleContextHolder.getLocale(), ex.getArgs());
        log.error("", ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .fields(ex.getFields())
                .errorMessage(msg)
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle the Multipart Exception
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({
            MultipartException.class,
    })
    public ResponseEntity<Object> handleMultipartException(Exception ex) {
        log.error("", ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle the Internal Server Error
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("", e);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage("Server Error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({
            SizeException.class
    })
    public ResponseEntity<Object> handleFileSizeLimitException(FileSizeLimitExceededException ex) {
        log.error("", ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.error("", ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMsg = String.format(StringUtils.collectionToCommaDelimitedString(errors));
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(errorMsg)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        log.error("", ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

}
