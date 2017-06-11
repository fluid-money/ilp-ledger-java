package money.fluid.ilp.ledger.config;

/**
 * Configuration for Ledger Websocket endpoints.
 *
 * @see "https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html#websocket-server"
 */
//@Configuration
//@EnableWebSocket
public class WebSocketConfig {//implements WebSocketConfigurer {

//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    MessageChannel clientInboundChannel;
//
//    @Autowired
//    SubscribableChannel clientOutboundChannel;

//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//
//        // TODO: Make these configurable via application.properties?
//        // TODO: Configure the idle timeout to some reasonable default.
//        //container.setMaxSessionIdleTimeout(4);
//        //container.setAsyncSendTimeout(4);
//
//        return container;
//    }

//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @Bean
//    public WebsocketSubscriptionHandler websocketSubscriptionHandler() {
//        return new WebsocketSubscriptionHandler(objectMapper, subscriptionRepository);
//    }

//    @Bean
//    public SubProtocolWebSocketHandler subProtocolWebSocketHandler() {
//        return new ILPWebsocketHandler(clientInboundChannel, clientOutboundChannel);
//    }

//    @Autowired
//    private TestILPSubProtocolHandler testILPSubProtocolHandler;
//
//    @Autowired
//    private ILPWebsocketHandler ilpWebsocketHandler;
//
//    @Override
//    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
//        registry
//                //.addHandler(websocketSubscriptionHandler(), "/websockets")
//                .addHandler(ilpWebsocketHandler, "/ilpclapi")
//                // TODO: Make this configurable, or figure out if it already is...
//                .setAllowedOrigins("*")
//                .addInterceptors(new HttpSessionHandshakeInterceptor());
//    }


}
