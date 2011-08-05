package jp.programmers.resource.adapter.twitter.inflow;

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

/**
 * TwitterActivationSpec
 */
@Activation(messageListeners = { jp.programmers.resource.adapter.twitter.inflow.TweetListener.class })
public class TwitterActivationSpec implements ActivationSpec {

    Logger log = Logger.getLogger("TwitterActivationSpec");
    ResourceAdapter ra;

    @ConfigProperty(defaultValue = "")
    private String query;

    public void validate() throws InvalidPropertyException {
    }

    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    public void setResourceAdapter(ResourceAdapter ra) {
        this.ra = ra;
    }

    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }

}
