package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Create Funding Withdrawal ({@code POST /funding/v1/withdrawals}), withdrawing to a saved address. The fee is either pinned by a {@code withdrawalFeeToken} from Calculate Funding Fees or the current fee, optionally capped by {@code maxFee}; omitting both uses the current fee.
 */
@Getter
@Builder(toBuilder = true)
public class CreateFundingWithdrawalParams extends FundingBetaParams {

    /**
     * The method or network to withdraw with. A network scope fails when the asset has several withdrawal methods on that network, e.g. USDC and USDC.e on Arbitrum.
     */
    @NonNull
    private final Scope scope;

    /**
     * The identifier of the saved address to withdraw to.
     */
    @NonNull
    private final String addressId;

    /**
     * The amount to withdraw and its asset.
     */
    @NonNull
    private final AssetAmount amount;

    /**
     * Whether the amount includes the fee. Required with {@code withdrawalFeeToken} or {@code maxFee}, and must match the setting used for the fee quote.
     */
    private final Boolean feeIncluded;

    /**
     * The {@code withdrawalFeeToken} of a fee quote less than 5 minutes old, pinning its fee rate. Mutually exclusive with {@code maxFee}.
     */
    private final String withdrawalFeeToken;

    /**
     * The maximum current fee accepted; the withdrawal fails if the fee exceeds it. Mutually exclusive with {@code withdrawalFeeToken}.
     */
    private final AssetAmount maxFee;

    /**
     * Whether the amount, the maximum fee and the quoted fee of tokenized assets use rebased or base units.
     */
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The crypto address stored for {@code addressId}; Kraken rejects the withdrawal if they differ.
     */
    private final String expectedAddress;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }

    /**
     * Returns the request body sent to Kraken.
     *
     * @return the body parameters
     * @throws IllegalArgumentException if the scope is a network group, if both {@code withdrawalFeeToken} and {@code maxFee} are set, or if one of them is set without {@code feeIncluded}
     */
    @Override
    protected Map<String, Object> body() {
        scope.requireMethodOrNetwork("Create Funding Withdrawal");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("scope", scope.json());
        body.put("address_id", addressId);
        body.put("amount", rebasable("asset_amount", amount.json()));
        putIfNonNull(body, "fee", fee());
        putIfNonNull(body, "expected_address", expectedAddress);
        return body;
    }

    private Map<String, Object> fee() {
        if (withdrawalFeeToken != null && maxFee != null) {
            throw new IllegalArgumentException("Specify at most one of withdrawalFeeToken or maxFee");
        }
        if (withdrawalFeeToken == null && maxFee == null && feeIncluded == null) {
            return null;
        }
        if (feeIncluded == null) {
            throw new IllegalArgumentException("feeIncluded is required with withdrawalFeeToken or maxFee");
        }

        Map<String, Object> fee = new LinkedHashMap<>();
        if (withdrawalFeeToken != null) {
            fee.put("quoted_fee", rebasable("token", withdrawalFeeToken));
        }
        else {
            Map<String, Object> currentFee = new LinkedHashMap<>();
            putIfNonNull(currentFee, "max_fee", maxFee == null ? null : rebasable("asset_amount", maxFee.json()));
            fee.put("current_fee", currentFee);
        }
        fee.put("fee_included", feeIncluded);
        return fee;
    }

    private Map<String, Object> rebasable(String key, Object value) {
        Map<String, Object> rebasable = new LinkedHashMap<>();
        rebasable.put(key, value);
        putIfNonNull(rebasable, "rebase_multiplier", rebaseMultiplier == null ? null : rebaseMultiplier.getValue());
        return rebasable;
    }
}
