package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Calculate Funding Fees ({@code GET /funding/v1/fees/{method_id}}); omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class FundingFeesParams extends FundingBetaParams {

    /**
     * The funding method to calculate fees for, sent in the path.
     */
    @NonNull
    private final String methodId;

    /**
     * The amount to calculate fees for.
     */
    @NonNull
    private final BigDecimal amount;

    /**
     * Whether the amount includes the fee.
     */
    private final Boolean feeIncluded;

    /**
     * A token of a previous withdrawal fee quote, valid for 5 minutes.
     */
    private final String withdrawalFeeToken;

    /**
     * Whether amounts of tokenized assets use rebased or base units.
     */
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("amount", amount.toPlainString());
        putIfNonNull(query, "fee_included", feeIncluded, String::valueOf);
        putIfNonNull(query, "withdrawal_fee_token", withdrawalFeeToken, v -> v);
        putIfNonNull(query, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
