package com.bwp.server;

import com.bwp.Main;
import com.bwp.data.Actor;
import com.bwp.data.config.Configs;
import com.bwp.data.config.TalentConfig;
import com.quiptmc.core.config.ConfigManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "OK");
        status.put("timestamp", OffsetDateTime.now().toString());
        String s = "" +
                "com.bwp.server.ApiController.addTalent(ApiController.java:65)" +
                "java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)" +
                "java.base/java.lang.reflect.Method.invoke(Method.java:580)" +
                "org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:207)" +
                "org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:152)" +
                "org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)" +
                "org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:884)" +
                "org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)" +
                "org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)" +
                "org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1081)" +
                "org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:974)" +
                "org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1011)" +
                "org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)" +
                "jakarta.servlet.http.HttpServlet.service(HttpServlet.java:649)" +
                "org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)" +
                "jakarta.servlet.http.HttpServlet.service(HttpServlet.java:710)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:130)" +
                "org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109)" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.springframework.boot.web.servlet.support.ErrorPageFilter.doFilter(ErrorPageFilter.java:124)" +
                "org.springframework.boot.web.servlet.support.ErrorPageFilter$1.doFilterInternal(ErrorPageFilter.java:99)" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)" +
                "org.springframework.boot.web.servlet.support.ErrorPageFilter.doFilter(ErrorPageFilter.java:117)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:109)" +
                "org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)" +
                "org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:79)" +
                "org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)" +
                "org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)" +
                "org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)" +
                "org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)" +
                "org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)" +
                "org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)" +
                "org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:396)" +
                "org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)" +
                "org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)" +
                "org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1780)" +
                "org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)" +
                "org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:948)" +
                "org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:482)" +
                "org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:57)" +
                "java.base/java.lang.Thread.run(Thread.java:1583)" +
                "";

        return status;
    }

    @GetMapping("/talents")
    public List<Actor> listTalents() {
        TalentConfig config = Configs.talent();
        return new ArrayList<>(config.talents.values());
    }

    @GetMapping("/talents/{id}")
    public Actor getTalent(@PathVariable("id") String id) {
        TalentConfig config = Configs.talent();
        try {
            Actor actor = config.talents.get(id);
            if (actor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
            }
            return actor;
        } catch (NoSuchMethodError | UnsupportedOperationException e) {
            // Fallback in case ConfigMap does not support get(String)
            for (Actor a : config.talents.values()) {
                try {
                    // Attempt to match via toString or fullName if needed; primary intent is id match
                    // If ConfigObject.id is not accessible, rely on map lookup above.
                } catch (Throwable ignored) {
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
        }
    }

    @GetMapping()
    public String root() {
        return "BWP Server API is running.";
    }

    @PostMapping("/talents/add")
    public Actor addTalent(@RequestParam("apiId") int apiId) {
        System.out.println("Received request to add talent with API ID: " + apiId);
        try {
            TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId, t);
        }
    }
}
