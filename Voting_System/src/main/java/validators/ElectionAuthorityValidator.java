package validators;

import domain.ElectionAuthority;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ElectionAuthorityValidator implements Validator<ElectionAuthority> {

    public void validateEmail(ElectionAuthority electionAuthority) {
        if(electionAuthority.getAuthorityEmail() == null || electionAuthority.getAuthorityEmail().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Authority is required.");
        }

        if (!electionAuthority.getAuthorityEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Authority email format.");
        }
    }

    public void validateName(ElectionAuthority electionAuthority) {
        if(electionAuthority.getAuthorityName() == null || electionAuthority.getAuthorityName().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name Authority is required.");
        }
    }

    @Override
    public void validate(ElectionAuthority electionAuthority) {
        if (electionAuthority == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authority cannot be null.");
        }
    }
}
