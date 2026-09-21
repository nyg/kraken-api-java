package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Asset;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * A page of funding methods returned by List Funding Methods.
 *
 * @param methods the funding methods of the page
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record FundingMethods(List<Method> methods,
                             @JsonProperty("next_cursor") String nextCursor) {

    /**
     * A deposit or withdrawal method for an asset on a network.
     *
     * @param asset the asset of the method
     * @param methodId the stable identifier of the method
     * @param methodName the display name of the method
     * @param minimumAmount the minimum amount per transaction, {@code null} if there is none
     * @param maximumAmount the maximum amount per transaction, {@code null} if there is none
     * @param fees the fees charged per transaction
     * @param network the network of the method, {@code null} for methods without one
     * @param deposit the deposit specific details, present only for deposit methods
     * @param withdrawal the withdrawal specific details, present only for withdrawal methods and currently empty
     */
    public record Method(Asset asset,
                         @JsonProperty("method_id") String methodId,
                         @JsonProperty("method_name") String methodName,
                         @JsonProperty("minimum_amount") BigDecimal minimumAmount,
                         @JsonProperty("maximum_amount") BigDecimal maximumAmount,
                         Fees fees,
                         Network network,
                         Deposit deposit,
                         Map<String, Object> withdrawal) {}

    /**
     * The fees charged per transaction by a funding method.
     *
     * @param base the fixed fee
     * @param percentage the percentage fee, {@code null} if there is none
     * @param included whether the fee is included in the transaction amount
     * @param min the minimum fee, {@code null} if there is none
     * @param max the maximum fee, {@code null} if there is none
     */
    public record Fees(AssetAmount base,
                       BigDecimal percentage,
                       boolean included,
                       AssetAmount min,
                       AssetAmount max) {}

    /**
     * The network used by a funding method.
     *
     * @param networkId the stable identifier of the network
     * @param networkName the network name
     * @param contractAddress the token contract address on the network, {@code null} for native assets
     * @param onChainAssetSymbol the asset symbol used on the network
     */
    public record Network(@JsonProperty("network_id") String networkId,
                          @JsonProperty("network_name") String networkName,
                          @JsonProperty("contract_address") String contractAddress,
                          @JsonProperty("on_chain_asset_symbol") String onChainAssetSymbol) {}

    /**
     * The details specific to a deposit method.
     *
     * @param sharesAddressesWithMethodId the method whose deposit addresses this method reuses, {@code null} if it has its own
     * @param addressSetupFee the fee charged when the first address is claimed, {@code null} if there is none
     * @param addressGeneration whether and how many addresses can be claimed
     * @param allowCreditCards whether credit cards are allowed, only provided for card methods; {@code false} means debit cards only
     * @param exemptedWithdrawalHold whether deposits are exempt from the withdrawal hold, only provided for methods imposing one
     */
    public record Deposit(@JsonProperty("shares_addresses_with_method_id") String sharesAddressesWithMethodId,
                          @JsonProperty("address_setup_fee") AssetAmount addressSetupFee,
                          @JsonProperty("address_generation") AddressGeneration addressGeneration,
                          @JsonProperty("allow_credit_cards") Boolean allowCreditCards,
                          @JsonProperty("exempted_withdrawal_hold") Boolean exemptedWithdrawalHold) {}

    /**
     * The availability of deposit address generation.
     *
     * @param status whether addresses can be generated, without or with a limit
     * @param limit the maximum number of addresses when the status is {@link Status#LIMITED}
     */
    public record AddressGeneration(Status status,
                                    Integer limit) {

        /**
         * Whether deposit addresses can be generated.
         */
        public enum Status {
            UNSUPPORTED,
            UNLIMITED,
            LIMITED,

            @JsonEnumDefaultValue
            UNKNOWN
        }
    }
}
