package jp.programmers.examples.ejb3.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.jboss.ejb3.annotation.ResourceAdapter;
import jp.programmers.resource.adapter.twitter.inflow.TweetListener;
import twitter4j.Tweet;

@MessageDriven(activationConfig={
   @ActivationConfigProperty(propertyName="query", propertyValue="jboss")
})
@ResourceAdapter("twitter-ra.rar")
public class TwitterMDB implements TweetListener {
    public void onTweet(Tweet t) {
        System.out.println(t);
    }
}
