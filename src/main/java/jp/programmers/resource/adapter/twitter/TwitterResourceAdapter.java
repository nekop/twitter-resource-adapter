package jp.programmers.resource.adapter.twitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import jp.programmers.resource.adapter.twitter.inflow.TwitterActivation;
import jp.programmers.resource.adapter.twitter.inflow.TwitterActivationSpec;

/**
 * TwitterResourceAdapter
 */
@Connector
public class TwitterResourceAdapter implements ResourceAdapter {

    Logger log = Logger.getLogger("TwitterResourceAdapter");
    ConcurrentHashMap<TwitterActivationSpec, TwitterActivation> activations =
        new ConcurrentHashMap<TwitterActivationSpec, TwitterActivation>();

    public void endpointActivation(MessageEndpointFactory endpointFactory,
                                   ActivationSpec spec)
        throws ResourceException {
        TwitterActivation activation = new TwitterActivation(this, endpointFactory, (TwitterActivationSpec)spec);
        activations.put((TwitterActivationSpec)spec, activation);
        activation.start();
    }

    public void endpointDeactivation(MessageEndpointFactory endpointFactory,
                                     ActivationSpec spec) {
        TwitterActivation activation = activations.remove(spec);
        if (activation != null) {
            activation.stop();
        }
    }

    public void start(BootstrapContext ctx)
        throws ResourceAdapterInternalException {

    }

    public void stop() {

    }

    public XAResource[] getXAResources(ActivationSpec[] specs)
        throws ResourceException {
        return null;
    }

}
