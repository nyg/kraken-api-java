package dev.andstuff.kraken.api.endpoint.earn.response;

/**
 * The response of the {@code Earn/AllocateStatus} and {@code Earn/DeallocateStatus} endpoints.
 *
 * @param pending whether an allocation or deallocation is still in progress on the strategy
 */
public record AllocationStatus(boolean pending) {}
