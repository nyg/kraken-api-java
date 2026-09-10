package dev.andstuff.kraken.api.endpoint.funding.response;


/**
 * The withdrawal address returned by {@code WithdrawAddresses}.
 *
 * @param address the address
 * @param asset the asset
 * @param method the method
 * @param key the key
 * @param tag the destination tag
 * @param memo the destination memo
 * @param verified the verified
 */
public record WithdrawalAddress(String address,
                                String asset,
                                String method,
                                String key,
                                String tag,
                                String memo,
                                Boolean verified) {}
