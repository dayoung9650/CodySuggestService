package musinsa.recruitmemt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
    public static ErrorResponse of(String code) {
        return new ErrorResponse(code, null);
    }
} 