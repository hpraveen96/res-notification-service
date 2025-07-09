package com.egov.commservice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1")
public class MainRestController
{
    private static final Logger logger = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    @Qualifier("projectRecordMessageWebClient")
    WebClient projectRecordMessageWebClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("write/message")
    public ResponseEntity<?> writeMessage(@RequestBody Message message,
                                          @RequestHeader("Authorization") String tokenValue,
                                          HttpServletRequest request,
                                          HttpServletResponse httpServletResponse)
    {
        // check for cookies first to identify whether this is a fresh request or a follow-up request


        // Cookie verification code
        List<Cookie> cookieList = null;
        //Optional<String> healthStatusCookie = Optional.ofNullable(request.getHeader("health_status_cookie"));
        Cookie[] cookies = request.getCookies();
        if(cookies == null)
        {
            cookieList = new ArrayList<>();
        }
        else
        {
            // REFACTOR TO TAKE NULL VALUES INTO ACCOUNT
            cookieList = List.of(cookies);
        }

        if( cookieList.stream().filter(cookie -> cookie.getName().equals("comm-service-key-1")).findAny().isEmpty()) // COOKIE_CHECK
        {
            logger.info("Received message: " + message.toString());
            String phone = null;
            try
            {
                phone =  tokenService.validateToken(tokenValue);
            }
            catch (WebClientResponseException e)
            {
                logger.info("Token validation failed: " + e.getMessage());
                return ResponseEntity.status(401).body("Invalid token");
            }

            logger.info("Token validation successful");

            logger.info("Phone number from token: " + phone);
            if(!phone.equals(message.getSenderId()))
            {
                logger.info("Phone number mismatch");
                return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
            }

            message.setTimestamp(Instant.now());
            message.setStatus("RECORDED");
            Message savedMessage = messageRepository.save(message);

            logger.info("Message saved: " + savedMessage.toString());

            MessageRecordView messageRecordView = new MessageRecordView();
            messageRecordView.setContext(message.getContext());
            messageRecordView.setContextId(message.getContextId());
            messageRecordView.setMessageId(savedMessage.getId());

            logger.info("MessageRecordView: " + messageRecordView.toString());

            logger.info("Forwarding message to project service");

            // forward an ASYNC request to the project service to record the message against the project or quote
            Mono<String> response = projectRecordMessageWebClient.post()
                    .uri("/api/v1/record/message")
                    .header("Authorization", tokenValue)
                    .bodyValue(messageRecordView)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(error -> logger.error("Error while forwarding message to project service: " + error.getMessage()));

            // Below is the handler for the response from the project service

            Integer stage2responseKey = new Random().nextInt();
            logger.info("Key for STAGE 2 response: " + stage2responseKey.toString());
            redisTemplate.opsForValue().set(stage2responseKey.toString(), "STAGE 1 COMPLETE");
            logger.info("Key for STAGE 2 response added to Redis");

            response.subscribe( responseString ->
                    {
                        logger.info("Response from project service: " + responseString);
                        // Response has to be put into the cache | Redis code will be written here
                        // The response will be written against a KEY which means the KEY has to be ready prior to this
                        redisTemplate.opsForValue().set(stage2responseKey.toString(), responseString);
                        logger.info("Response from project service added to Redis");
                    },
                    error -> {
                        logger.error("Error while forwarding message to project service: " + error.getMessage());
                    });

            logger.info("Message forwarded to project service");
            // The above particular section will be executed asynchronously, allowing the main thread to continue processing.

            // The Key has to be added to the outgoing response as a cookie  [or a header]
            Cookie cookie = new Cookie("comm-service-key-1", stage2responseKey.toString());
            logger.info("Cookie created with key: " + cookie.getName() + " and value: " + cookie.getValue());
            httpServletResponse.addCookie(cookie);
            logger.info("Cookie added to response");

            return ResponseEntity.ok("Message SAVED successfully - STAGE 1 Complete - STAGE 2 In Progress");
        }
        else
        {
            // This is a follow-up request
            // The Key has to be read from the incoming request as a cookie  [or a header]
            String stage2responseKey = cookieList.stream().filter(cookie -> cookie.getName().equals("comm-service-key-1")).findAny().get().getValue();
            logger.info("Key for STAGE 2 response: " + stage2responseKey.toString());
            String responseString = (String) redisTemplate.opsForValue().get(stage2responseKey);
            logger.info("Response from Redis: " + responseString);

            if(responseString.equals("STAGE 1 COMPLETE"))
            {
                logger.info("Stage 1 complete, stage 2 in progress | waiting for response from project service");
                return ResponseEntity.ok("Message SAVED successfully - STAGE 1 Complete - STAGE 2 In Progress");
            }
            else
            {
                logger.info(responseString);
                return ResponseEntity.ok(responseString);
            }

        }
    }

    @PostMapping("read/messages")
    public ResponseEntity<?> getMessages(@RequestBody MessagePage messagePage)
    {
        List<Optional<Message>> messages = messagePage.getMessagesList().stream().map(messageid -> messageRepository.findById(messageid)).toList();
        return ResponseEntity.ok(messages.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

}
