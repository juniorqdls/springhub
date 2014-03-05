package com.mjeanroy.springhub.test.servers;

import static org.eclipse.jetty.util.resource.Resource.newResource;

import javax.servlet.ServletContainerInitializer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.annotations.AbstractDiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationDecorator;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.annotations.WebFilterAnnotationHandler;
import org.eclipse.jetty.annotations.WebListenerAnnotationHandler;
import org.eclipse.jetty.annotations.WebServletAnnotationHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded Jetty Implementation.
 */
public class EmbeddedJetty implements EmbeddedServer {

	/**
	 * Class logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(EmbeddedJetty.class);

	/**
	 * Jetty Server.
	 */
	protected Server server;

	/**
	 * Flag to know if jetty is started.
	 */
	protected boolean started;

	/**
	 * Webapp directory.
	 */
	protected String webapp;

	private ServerConnector serverConnector;

	/**
	 * Create embedded jetty with :
	 * - Random port.
	 * - Default webapp directory (src/main/webapp).
	 */
	public EmbeddedJetty() {
		this(0);
	}

	/**
	 * Create embedded jetty with :
	 * - Given port.
	 * - Default webapp directory (src/main/webapp).
	 *
	 * @param port Port.
	 */
	public EmbeddedJetty(int port) {
		this("src/main/webapp", port);
	}

	/**
	 * Create embedded jetty with :
	 * - Given port.
	 * - Given webapp directory.
	 *
	 * @param webapp Webapp directory.
	 * @param port Port.
	 */
	public EmbeddedJetty(String webapp, int port) {
		log.debug("Create embedded jetty");
		server = new Server(port);
		server.setStopAtShutdown(true);
		server.setStopTimeout(10000);

		this.webapp = webapp;
	}

	@Override
	public void start() {
		if (!isStarted()) {
			log.debug("Start embedded jetty");

			try {
				String contextPath = "/";

				log.debug("Create webapp context");
				log.debug("- Webapp : {}", webapp);
				log.debug("- Context path : {}", contextPath);

				WebAppContext ctx = new WebAppContext();
				ctx.setContextPath(contextPath);

				// Useful for WebXmlConfiguration
				ctx.setBaseResource(newResource(webapp));

				ctx.setClassLoader(Thread.currentThread().getContextClassLoader());

				ctx.setConfigurations(new Configuration[]{
						new WebXmlConfiguration(),
						new AnnotationConfiguration()
				});

				ctx.getMetaData().addContainerResource(new FileResource(new File("./target/classes").toURI()));

				ctx.setParentLoaderPriority(true);
				ctx.setWar(webapp);
				ctx.setServer(server);

				server.setHandler(ctx);

				Thread thread = new Thread() {
					public void run() {
						try {
							server.start();
						}
						catch (Exception ex) {
							log.error("Error when stopping Jetty server: " + ex.getMessage(), ex);
						}
					}
				};

				thread.start();
				thread.join();
				thread.interrupt();

				started = true;
			}
			catch (Exception ex) {
				log.error(ex.getMessage());
				throw new EmbeddedServerException(ex);
			}
		}
	}

	@Override
	public void stop() {
		if (isStarted()) {
			try {
				Thread thread = new Thread() {
					public void run() {
						try {
							log.debug("Stop embedded jetty");
							server.stop();
							server.join();
							log.debug("Embedded jetty is now stopped");
						}
						catch (Exception ex) {
							log.error("Error when stopping Jetty server: " + ex.getMessage(), ex);
						}
					}
				};

				thread.start();
				thread.join();
				thread.interrupt();
			}
			catch (Exception ex) {
				log.error(ex.getMessage());
				throw new EmbeddedServerException(ex);
			}
			finally {
				started = false;
			}
		}
	}

	@Override
	public void restart() {
		stop();
		start();
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public int getPort() {
		if (serverConnector == null) {
			serverConnector = findServerConnector();
		}
		return serverConnector.getLocalPort();
	}

	private ServerConnector findServerConnector() {
		for (Connector connector : server.getConnectors()) {
			if (connector instanceof ServerConnector) {
				return (ServerConnector) connector;
			}
		}
		return null;
	}
}
