package ua.com.fielden.platform.web.resources;

import java.io.ByteArrayInputStream;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Encoding;
import org.restlet.engine.application.EncodeRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.common.base.Charsets;

import ua.com.fielden.platform.web.app.IPreloadedResources;

/**
 *
 * Responds to GET requests with generated application specific main Web UI component, which basically represents a scaffolding for the whole application Web UI client.
 *
 * @author TG Team
 *
 */
public class MainWebUiComponentResource extends ServerResource {
    private final IPreloadedResources preloadedResources;

    public MainWebUiComponentResource(final IPreloadedResources preloadedResources, final Context context, final Request request, final Response response) {
        init(context, request, response);
        this.preloadedResources = preloadedResources;
    }

    @Override
    protected Representation get() throws ResourceException {
        final String source = preloadedResources.getSourceOnTheFly("/app/tg-app.html");
        return new EncodeRepresentation(Encoding.GZIP, new InputRepresentation(new ByteArrayInputStream(source.getBytes(Charsets.UTF_8))));
    }
}
