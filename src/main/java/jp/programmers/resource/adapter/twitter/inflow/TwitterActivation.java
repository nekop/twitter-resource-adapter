package jp.programmers.resource.adapter.twitter.inflow;

import jp.programmers.resource.adapter.twitter.TwitterResourceAdapter;

import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;

/**
 * TwitterActivation
 */
public class TwitterActivation {

    Logger log = Logger.getLogger("TwitterActivation");
    TwitterResourceAdapter ra;
    TwitterActivationSpec spec;
    MessageEndpointFactory endpointFactory;

    public TwitterActivation() throws ResourceException {
        this(null, null, null);
    }

    public TwitterActivation(TwitterResourceAdapter ra, 
                             MessageEndpointFactory endpointFactory,
                             TwitterActivationSpec spec) throws ResourceException {
        this.ra = ra;
        this.endpointFactory = endpointFactory;
        this.spec = spec;
    }

    public TwitterActivationSpec getActivationSpec() {
        return spec;
    }

    public MessageEndpointFactory getMessageEndpointFactory() {
        return endpointFactory;
    }

    public void start() throws ResourceException {

    }

    public void stop() {

    }

}
