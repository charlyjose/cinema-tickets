package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class AccountValidationService {

    /**
     * Wrapper method to validate Account Details, could be extended to validate
     * other details related to account
     * 
     * @param accountId account Id
     * 
     */
    public void validate(Long accountId) {
        validateAccountId(accountId);
    }

    /**
     * Validates account Id is greater than 0 and not null
     * 
     * @param accountId account Id
     * @throws InvalidPurchaseException invalid purchase exception
     * 
     */
    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid Account Id");
        }
    }
}
