package jp.programmers.resource.adapter.twitter.inflow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import static java.util.logging.Level.*;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import jp.programmers.resource.adapter.twitter.TwitterResourceAdapter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * TwitterActivation
 */
public class TwitterActivation extends TimerTask implements XAResource {

    Logger log = Logger.getLogger("TwitterActivation");
    TwitterResourceAdapter ra;
    TwitterActivationSpec spec;
    MessageEndpointFactory endpointFactory;
    MessageEndpoint endpoint;
    Twitter twitter;
    Timer timer;
    long lastId;

    static final Method ON_TWEET;
    static {
        try {
            ON_TWEET = TweetListener.class.getMethod("onTweet", new Class[] { Tweet.class });
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

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
        this.twitter = new TwitterFactory().getInstance();
        endpoint = endpointFactory.createEndpoint(this);
        timer = ra.getBootstrapContext().createTimer();
        timer.schedule(this, 0L, spec.getInterval());
    }

    public void stop() {
        this.cancel();
        timer.cancel();
        endpoint.release();
        this.twitter = null;
        this.endpoint = null;
        this.timer = null;
    }

    public void run() {
        log.log(FINE, "Twitter search query={0}", spec.getQuery());
        Query query = new Query(spec.getQuery());
        query.setSinceId(lastId);
        try {
            QueryResult result = twitter.search(query);
            List<Tweet> tweets = result.getTweets();
            if (tweets != null && tweets.size() > 0) {
                lastId = tweets.get(0).getId();
            }
            log.log(FINE, "{0} results found, lastId={1}", new Object[] {tweets.size(), lastId});
            // The list is newest first but we need oldest first
            // Reverse iterate
            for (ListIterator<Tweet> it = tweets.listIterator(tweets.size()); it.hasPrevious(); ) {
                Tweet tweet = it.previous();
                try {
                    endpoint.beforeDelivery(ON_TWEET);
                    try {
                        ((TweetListener)endpoint).onTweet(tweet);
                    } finally {
                        endpoint.afterDelivery();
                    }
                } catch (Throwable t) {
                    log.log(INFO, "Error in message listener", t);
                }
            }
        } catch (TwitterException ex) {
            log.log(INFO, "Failed to perform search", ex);
        }
    }


    // XAResource implementation (a bad implementation)

    public void start(Xid xid, int flags) {
    }

    public void end(Xid xid, int flags) {
    }

    public int prepare(Xid xid) {
        return XAResource.XA_OK;
    }

    public void rollback(Xid xid) {
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
    }

    public void forget(Xid xid) {
    }

    public Xid[] recover(int flag) {
        return new Xid[0];
    }

    public int getTransactionTimeout() {
        return 0;
    }

    public boolean setTransactionTimeout(int seconds) {
        return false;
    }

    public boolean isSameRM(XAResource xares) {
        return (xares == this);
    }
}
