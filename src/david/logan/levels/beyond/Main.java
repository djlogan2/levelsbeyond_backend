package david.logan.levels.beyond;

import java.net.URI;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("")
public class Main extends Application {
	public static class JerseyApp extends ResourceConfig {
		public JerseyApp() {
			register(NotesImpl.class);
		}
	};
	
    public static void main(String[] args) throws Exception {
	    	URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
	    Server server = JettyHttpContainerFactory.createServer(baseUri, new JerseyApp(), true);
	    server.start();
	    server.join();
    	}
}
