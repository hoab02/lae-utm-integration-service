package com.viettelpost.fms.utm_integration.exception;

import com.viettelpost.fms.common.dto.BaseErrorDto;
import com.viettelpost.fms.common.i18n.I18nMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.slf4j.MDC;
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
import org.springframework.web.context.request.ServletWebRequest;
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

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                "ACCESS_DENIED",
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
        String msg = i18nMessageService.translate("error.permission");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
    }

    @ExceptionHandler({InternalException.class})
    public ResponseEntity<Object> handleInternalException(InternalException ex, WebRequest request) {
        String msg = i18nMessageService.translate(ex.getMessage(), LocaleContextHolder.getLocale(), ex.getArgs());
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                ex.getErrorCode(),
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .fields(ex.getFields())
                .errorMessage(msg)
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<Object> handleMultipartException(Exception ex, WebRequest request) {
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                "MULTIPART_ERROR",
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGenericException(Exception e, WebRequest request) {
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                e.getClass().getSimpleName(),
                "UNEXPECTED_ERROR",
                getRequestUri(request),
                MDC.get("traceId"),
                e);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage("Server Error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({SizeException.class})
    public ResponseEntity<Object> handleFileSizeLimitException(FileSizeLimitExceededException ex, WebRequest request) {
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                "FILE_SIZE_LIMIT",
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
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
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                "VALIDATION_ERROR",
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
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
        log.error("request_failed type={} errorCode={} uri={} traceId={}",
                ex.getClass().getSimpleName(),
                "MISSING_PARAMETER",
                getRequestUri(request),
                MDC.get("traceId"),
                ex);
        BaseErrorDto error = BaseErrorDto.builder()
                .timestamp(new Date())
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    private String getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "unknown";
    }
}