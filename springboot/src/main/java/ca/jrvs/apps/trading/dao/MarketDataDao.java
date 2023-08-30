package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MarketDataDao implements CrudRepository<IexQuote, String> {
    private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
    private final String IEX_BATCH_URL;

    private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
    private HttpClientConnectionManager httpClientConnectionManager;

    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager,
                         MarketDataConfig marketDataConfig) {
        this.httpClientConnectionManager = httpClientConnectionManager;
        IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
    }

    // Not necessary
    @Override
    public <S extends IexQuote> S save(S s) {
        return null;
    }

    // Not necessary
    @Override
    public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    /**
     * Get an IexQuote (helper method with class findAllById)
     * @param ticker
     * @throws IllegalArgumentException if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     */
    @NonNull
    @Override
    public Optional<IexQuote> findById(String ticker) {
        Optional<IexQuote> iexQuote;
        List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));

        if (quotes.isEmpty()) {
            return Optional.empty();
        } else if (quotes.size() == 1) {
            iexQuote = Optional.of(quotes.get(0));
        } else {
            throw new DataRetrievalFailureException("Unexpected number of quotes");
        }
        return iexQuote;
    }
    // Not necessary
    @Override
    public boolean existsById(String s) {
        return false;
    }

    // Not necessary
    @Override
    public Iterable<IexQuote> findAll() {
        return null;
    }

    /**
     * Get quotes from IEX
     * @param tickers is a list of tickers
     * @return a list of IexQuote object
     * @throws IllegalArgumentException if any ticker is invalid or tickers is empty
     * @throws DataRetrievalFailureException if HTTP request failed
     */
    @NonNull
    @Override
    public List<IexQuote> findAllById(Iterable<String> tickers) {
        String concatenatedTickers = Stream.of(tickers).map(Objects::toString).collect(Collectors.joining(","));
        String encodedTickers = "";
        encodedTickers = concatenatedTickers
                .replaceAll("\\[","")
                .replaceAll(" ", "")
                .replaceAll("\\]","");
        String response = String.valueOf(executeHttpGet(String.format(IEX_BATCH_URL, encodedTickers))
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticker")));
        System.out.println(response);
        JSONObject iexQuotesJson = new JSONObject(response);
        if (iexQuotesJson.isEmpty()) {
            throw new IllegalArgumentException("Invalid ticker");
        }
        System.out.println(iexQuotesJson);
        JSONArray arr = iexQuotesJson.toJSONArray(iexQuotesJson.names());
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            jsonObjects.add(arr.getJSONObject(i).getJSONObject("quote"));
        }
        JSONObject a = arr.getJSONObject(0);
        ObjectMapper mapper = new ObjectMapper();


        return jsonObjects.stream().map(i -> {
            IexQuote quote;
            try {
                quote = mapper.readValue(i.toString(), IexQuote.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return quote;
        }).collect(Collectors.toList());
    }

    // Not necessary
    @Override
    public long count() {
        return 0;
    }

    // Not necessary
    @Override
    public void deleteById(String s) {

    }

    // Not necessary
    @Override
    public void delete(IexQuote iexQuote) {

    }

    // Not necessary
    @Override
    public void deleteAll(Iterable<? extends IexQuote> iterable) {

    }

    // Not necessary
    @Override
    public void deleteAll() {

    }

    /**
     * Execute a get and return http entity/body as a string
     * Tip: use EntityUtils.toString to process HTTP entity
     * @param url resource URL
     * @return http response boy or Optional.empty for 404 response
     * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
     */
    private Optional<String> executeHttpGet(String url) {
        HttpResponse httpResponse;
        String result = "";
        try (CloseableHttpClient httpClient = getHttpClient()){
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity);
            } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.NOT_FOUND.value()){
                return Optional.empty();
            } else {
                throw new DataRetrievalFailureException("Failed to to get http response");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(result);
    }

    /**
     * Borrow an HTTP client form the httpClientConnectionManager
     * @return an httpClient
     */
    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setConnectionManagerShared(true)
                .build();
    }
}
