package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The networks and network groups returned by List Funding Networks.
 *
 * @param networkGroups the groups of networks sharing an address format, e.g. EVM networks
 * @param networks the networks available for funding
 */
public record FundingNetworks(@JsonProperty("network_groups") List<NetworkGroup> networkGroups,
                              List<Network> networks) {

    /**
     * A group of networks sharing compatible addresses.
     *
     * @param networkGroupId the stable identifier of the group
     * @param name the group name
     * @param networkIds the identifiers of the networks of the group
     */
    public record NetworkGroup(@JsonProperty("network_group_id") String networkGroupId,
                               String name,
                               @JsonProperty("network_ids") List<String> networkIds) {}

    /**
     * A network available for funding.
     *
     * @param networkId the stable identifier of the network
     * @param name the network name
     */
    public record Network(@JsonProperty("network_id") String networkId,
                          String name) {}
}
