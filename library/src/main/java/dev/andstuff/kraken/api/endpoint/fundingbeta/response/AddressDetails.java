package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The details of a deposit or withdrawal address of the Funding (Beta) API. Exactly one of the components is set; Kraken only sends the fields that apply to the address.
 *
 * @param crypto the details of a crypto address
 * @param fiat the details of a fiat address
 */
public record AddressDetails(Crypto crypto,
                             Fiat fiat) {

    /**
     * The details of a crypto address.
     *
     * @param address the crypto address
     * @param tag the destination tag, on blockchains using one
     * @param memo the memo, on blockchains using one
     * @param beneficiary the beneficiary of a withdrawal address
     */
    public record Crypto(String address,
                         String tag,
                         String memo,
                         Beneficiary beneficiary) {}

    /**
     * The details of a fiat address: a bank account for deposits, a bank account, card or payment account for withdrawals.
     *
     * @param account the bank account number
     * @param accountType the bank account type
     * @param address the bank or wallet address
     * @param bank the bank name
     * @param bankCode the bank code
     * @param bic the bank identifier code
     * @param branch the bank branch
     * @param branchCode the bank branch code
     * @param bankAddress the bank address
     * @param bsb the Bank State Branch code
     * @param beneficiary the beneficiary of a withdrawal address
     * @param cardNumber the card number
     * @param cardNumberLast the last four digits of the card number
     * @param cardType the card type
     * @param cvc the card verification code
     * @param expireMonth the card expiration month
     * @param expireYear the card expiration year
     * @param iban the International Bank Account Number
     * @param maskedIban the masked IBAN
     * @param intermediaryAddress the intermediary bank address
     * @param intermediaryBank the intermediary bank name
     * @param intermediaryBranch the intermediary bank branch
     * @param intermediaryRouting the intermediary bank routing number
     * @param intermediarySwift the intermediary bank SWIFT or BIC code
     * @param memo the payment memo
     * @param merchantReference the merchant reference
     * @param nameOnAccount the account holder name
     * @param notes additional notes
     * @param password the account password
     * @param routing the bank routing number
     * @param sort the bank sort code
     * @param swift the SWIFT code
     * @param tag the destination tag
     * @param thirdPartyEmail the third-party email address
     * @param transactionId the payment provider transaction identifier
     * @param transit the bank transit number
     * @param username the account username
     * @param signature the digital signature proving address ownership
     */
    public record Fiat(String account,
                       @JsonProperty("account_type") String accountType,
                       String address,
                       String bank,
                       @JsonProperty("bank_code") String bankCode,
                       String bic,
                       String branch,
                       @JsonProperty("branch_code") String branchCode,
                       @JsonProperty("bank_address") String bankAddress,
                       String bsb,
                       Beneficiary beneficiary,
                       @JsonProperty("card_number") String cardNumber,
                       @JsonProperty("card_number_last") String cardNumberLast,
                       @JsonProperty("card_type") String cardType,
                       String cvc,
                       @JsonProperty("expire_month") String expireMonth,
                       @JsonProperty("expire_year") String expireYear,
                       String iban,
                       @JsonProperty("masked_iban") String maskedIban,
                       @JsonProperty("intermediary_address") String intermediaryAddress,
                       @JsonProperty("intermediary_bank") String intermediaryBank,
                       @JsonProperty("intermediary_branch") String intermediaryBranch,
                       @JsonProperty("intermediary_routing") String intermediaryRouting,
                       @JsonProperty("intermediary_swift") String intermediarySwift,
                       String memo,
                       @JsonProperty("merchant_reference") String merchantReference,
                       @JsonProperty("name_on_account") String nameOnAccount,
                       String notes,
                       String password,
                       String routing,
                       String sort,
                       String swift,
                       String tag,
                       @JsonProperty("third_party_email") String thirdPartyEmail,
                       @JsonProperty("transaction_id") String transactionId,
                       String transit,
                       String username,
                       String signature) {}

    /**
     * The beneficiary of a withdrawal address.
     *
     * @param recipient whether the beneficiary is the user ({@code sender}) or another party ({@code other})
     * @param type the beneficiary type, {@code individual} or {@code business}
     * @param name the beneficiary first name
     * @param lastName the beneficiary last name
     * @param country the beneficiary country
     * @param stateProvince the beneficiary state or province
     * @param address1 the first line of the beneficiary address
     * @param address2 the second line of the beneficiary address
     * @param city the beneficiary city
     * @param postalCode the beneficiary postal code
     * @param counterpartyVaspName the name of the counterparty virtual asset service provider
     */
    public record Beneficiary(String recipient,
                              @JsonProperty("typ") String type,
                              String name,
                              @JsonProperty("last_name") String lastName,
                              String country,
                              @JsonProperty("state_province") String stateProvince,
                              @JsonProperty("address_1") String address1,
                              @JsonProperty("address_2") String address2,
                              String city,
                              @JsonProperty("postal_code") String postalCode,
                              @JsonProperty("counterparty_vasp_name") String counterpartyVaspName) {}
}
