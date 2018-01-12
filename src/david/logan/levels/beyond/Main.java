package david.logan.levels.beyond;

import java.net.URI;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("api/notes")
public class Main {
    public static void main(String[] args) throws Exception {
	    	URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
	    	ResourceConfig config = new ResourceConfig(); config.register(NotesImpl.class);
	    Server server = JettyHttpContainerFactory.createServer(baseUri, config, true);
	    server.start();
	    server.join();
    	}
}
