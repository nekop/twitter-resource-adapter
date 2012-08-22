package jp.programmers.resource.adapter.twitter;

import java.util.Iterator;
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
    BootstrapContext context;
    
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

    public void start(BootstrapContext context)
        throws ResourceAdapterInternalException {
        this.context = context;
    }

    public void stop() {
        for (Iterator<TwitterActivation> it = activations.values().iterator(); it.hasNext();) {
            TwitterActivation activation = it.next();
            activation.stop();
            it.remove();
        }
    }

    public XAResource[] getXAResources(ActivationSpec[] specs)
        throws ResourceException {
        return null;
    }

    public BootstrapContext getBootstrapContext() {
        return context;
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

}
