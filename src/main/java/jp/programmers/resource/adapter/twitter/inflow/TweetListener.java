package jp.programmers.resource.adapter.twitter.inflow;

import twitter4j.Tweet;

/**
 * TweetListener
 */
public interface TweetListener {
    public void onTweet(Tweet tweet);
}
