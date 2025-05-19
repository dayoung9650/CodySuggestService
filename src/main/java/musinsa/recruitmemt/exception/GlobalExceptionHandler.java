package musinsa.recruitmemt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private boolean isApiRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/") || 
               request.getHeader("Accept") != null && 
               request.getHeader("Accept").contains("application/json");
    }

    @ExceptionHandler(NoItemsFoundException.class)
    public Object handleNoItemsFoundException(NoItemsFoundException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("NO_ITEMS_FOUND", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PriceNotDefinedException.class)
    public Object handlePriceNotDefinedException(PriceNotDefinedException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("PRICE_NOT_DEFINED", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public Object handleItemNotFoundException(ItemNotFoundException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("ITEM_NOT_FOUND", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public Object handleCategoryNotFoundException(CategoryNotFoundException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("CATEGORY_NOT_FOUND", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public Object handleBrandNotFoundException(BrandNotFoundException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("BRAND_NOT_FOUND", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("INVALID_ARGUMENT", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return createErrorModelAndView(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            ErrorResponse response = ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createErrorModelAndView("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView createErrorModelAndView(String message, HttpStatus status) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error/error");
        mav.addObject("message", message);
        mav.addObject("status", status.value());
        return mav;
    }
} 