package net.gaiait.divination.reading;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class ReadingRequestValidatorImpl implements ReadingRequestValidator {

    public static final String NULL_ERROR = "null";
    public static final String SUBJECT_EMPTY_ERROR = "subject.empty";
    public static final String HEXAGRAM_RANGE_ERROR = "hexagramNumber.range";
    public static final String DIVINER_EMPTY_ERROR = "diviner.empty";

    @Override
    public boolean supports(Class<?> clazz) {
        return ReadingRequestImpl.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        if (obj == null) {
            e.reject(NULL_ERROR);
            return;
        }
        ValidationUtils.rejectIfEmpty(e, ReadingRequestImpl.SUBJECT_FIELD, SUBJECT_EMPTY_ERROR);

        ReadingRequest req = (ReadingRequest) obj;
        validateHexagram(req.getHexagramNo(), e);
        
        req.getDiviner().ifPresent(d -> validateDiviner(d, e));
    }
    
    private static void validateHexagram(int i, Errors e) {
        if (!Hexagram.validateHexagramNo(i)) {
            e.rejectValue(ReadingRequestImpl.HEXAGRAM_FIELD, HEXAGRAM_RANGE_ERROR);
        }
    }

    private static void validateDiviner(String d, Errors e) {
        if (StringUtils.isEmpty(d)) {
            e.rejectValue(ReadingRequestImpl.DIVINER_FIELD, DIVINER_EMPTY_ERROR);
        }
    }

}
