package com.manual.manual.exception;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public BaseResponse<?> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException", e);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, "上传文件不能超过100M");
    }

    @ExceptionHandler(MultipartException.class)
    public BaseResponse<?> multipartExceptionHandler(MultipartException e) {
        log.error("MultipartException", e);
        if (containsUploadSizeExceeded(e)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "上传文件不能超过100M");
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "System error");
    }

    @ExceptionHandler(IllegalStateException.class)
    public BaseResponse<?> illegalStateExceptionHandler(IllegalStateException e) {
        log.error("IllegalStateException", e);
        if (containsUploadSizeExceeded(e)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "上传文件不能超过100M");
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "System error");
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "System error");
    }

    private boolean containsUploadSizeExceeded(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof MaxUploadSizeExceededException
                    || current.getClass().getName().contains("FileSizeLimitExceededException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
