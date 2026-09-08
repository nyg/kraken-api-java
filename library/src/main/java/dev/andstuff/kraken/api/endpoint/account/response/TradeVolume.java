package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The trade volume returned by {@code TradeVolume}.
 *
 * @param currency the currency
 * @param assetClass the asset class
 * @param volume the volume
 * @param inputs the inputs
 * @param fees the fees
 * @param feesMaker the fees maker
 * @param volumeSubaccounts the volume subaccounts
 * @param schedules the schedules
 */
public record TradeVolume(String currency,
        @JsonProperty("asset_class") AssetClass assetClass,
        BigDecimal volume,
        Inputs inputs,
        Map<String, FeeTier> fees,
        @JsonProperty("fees_maker") Map<String, FeeTier> feesMaker,
        @JsonProperty("volume_subaccounts") List<SubaccountVolume> volumeSubaccounts,
        List<FeeSchedule> schedules) {

    /**
     * The asset class values used by the {@code TradeVolume} endpoint.
     */
    public enum AssetClass {
        @JsonProperty("volume") VOLUME,
        @JsonProperty("currency") CURRENCY,
        @JsonProperty("forex") FOREX,
        @JsonProperty("equity") EQUITY,
        @JsonProperty("equity_pair") EQUITY_PAIR,
        @JsonProperty("nft") NFT,
        @JsonProperty("derivatives") DERIVATIVES,
        @JsonProperty("tokenized_asset") TOKENIZED_ASSET,
        @JsonProperty("futures_contract") FUTURES_CONTRACT,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The inputs returned by {@code TradeVolume}.
     *
     * @param domainSpotVolume30d the domain spot volume30d
     * @param domainFuturesVolume30d the domain futures volume30d
     * @param domainAssetsOnPlatform the domain assets on platform
     */
    public record Inputs(@JsonProperty("domain_spot_volume_30d") BigDecimal domainSpotVolume30d,
            @JsonProperty("domain_futures_volume_30d") BigDecimal domainFuturesVolume30d,
            @JsonProperty("domain_assets_on_platform") BigDecimal domainAssetsOnPlatform) {}

    /**
     * The fee tier returned by {@code TradeVolume}.
     *
     * @param fee the fee
     * @param minimumFee the minimum fee
     * @param maximumFee the maximum fee
     * @param nextFee the next fee
     * @param tierVolume the tier volume
     * @param tierFuturesVolume the tier futures volume
     * @param nextVolume the next volume
     * @param nextFuturesVolume the next futures volume
     * @param volumeOffset the volume offset
     */
    public record FeeTier(BigDecimal fee,
            @JsonProperty("minfee") BigDecimal minimumFee,
            @JsonProperty("maxfee") BigDecimal maximumFee,
            @JsonProperty("nextfee") BigDecimal nextFee,
            @JsonProperty("tiervolume") BigDecimal tierVolume,
            @JsonProperty("tierfuturesvolume") BigDecimal tierFuturesVolume,
            @JsonProperty("nextvolume") BigDecimal nextVolume,
            @JsonProperty("nextfuturesvolume") BigDecimal nextFuturesVolume,
            @JsonProperty("volumeoffset") BigDecimal volumeOffset) {}

    /**
     * The subaccount volume returned by {@code TradeVolume}.
     *
     * @param iiban the iiban
     * @param volume the volume
     */
    public record SubaccountVolume(String iiban,
            BigDecimal volume) {}

    /**
     * The tier returned by {@code TradeVolume}.
     *
     * @param makerFee the maker fee
     * @param takerFee the taker fee
     * @param minSpotVolume the min spot volume
     * @param minFuturesVolume the min futures volume
     * @param minAssetsOnPlatform the min assets on platform
     * @param active the active
     */
    public record Tier(@JsonProperty("maker_fee") BigDecimal makerFee,
            @JsonProperty("taker_fee") BigDecimal takerFee,
            @JsonProperty("min_spot_volume") BigDecimal minSpotVolume,
            @JsonProperty("min_futures_volume") BigDecimal minFuturesVolume,
            @JsonProperty("min_assets_on_platform") BigDecimal minAssetsOnPlatform,
            Boolean active) {}

    /**
     * The fee schedule returned by {@code TradeVolume}.
     *
     * @param pair the pair
     * @param assetClass the asset class
     * @param tiers the tiers
     */
    public record FeeSchedule(String pair,
            @JsonProperty("class") AssetClass assetClass,
            List<Tier> tiers) {}
}
