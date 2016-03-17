package net.kear.recipeorganizer.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {

    private final int MAX_PASSWORD_ATTEMPT = 3;
    private final int PASSWORD_EXPIRATION_MINUTES = 1;
    private LoadingCache<String, Integer> badPasswordCache;
	
	public LoginAttemptService() {
		badPasswordCache = CacheBuilder.newBuilder().expireAfterWrite(PASSWORD_EXPIRATION_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(final String key) {
                return 0;
            }
        });
	}

    public void loginSucceeded(final String key) {
   		badPasswordCache.invalidate(key);
    }

    public void loginFailed(final String key) {
        int attempts = 0;
        try {
            attempts = badPasswordCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        badPasswordCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
    	/*int attempts = 0;
        try {
        	attempts = badPasswordCache.get(key);
        } catch (final ExecutionException e) {
            return false;
        }
        return attempts >= MAX_PASSWORD_ATTEMPT;*/

        try {
            return badPasswordCache.get(key) >= MAX_PASSWORD_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}
