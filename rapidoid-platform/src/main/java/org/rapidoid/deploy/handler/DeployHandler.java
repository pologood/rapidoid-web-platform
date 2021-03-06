/*-
 * #%L
 * rapidoid-platform
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.rapidoid.deploy.handler;


import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.http.Current;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.u.U;
import org.rapidoid.util.Tokens;

import java.util.List;
import java.util.Map;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DeployHandler extends GUI implements ReqHandler {

	private static final String SCOPE = "POST:/rapidoid/stage, POST:/rapidoid/deploy, GET:POST:/rapidoid/deployment";

	private static final String DEPLOYMENT_HELP = "Application deployment works by uploading a JAR and executing it in a child Java process.";

	@Override
	public Object execute(Req req) throws Exception {
		List<Object> info = U.list();

		info.add(h4(DEPLOYMENT_HELP));

		Map<String, String> tokenData = U.map(Tokens._USER, Current.username(), Tokens._SCOPE, SCOPE);
		String token = Tokens.serialize(tokenData);

		info.add(h3("Your deployment token is:"));
		info.add(copy(textarea(token).rows("2").attr("readonly", "readonly").style("width:100%; font-size: 10px;")));

		info.add(h3("Upload an application JAR to stage it and then deploy it:"));
		info.add(render("upload-jar.html"));

		info.add(h3("Packaging and deploying with Maven:"));

		String cmd = "mvn clean org.rapidoid:app:deploy";
		info.add(copy(textarea(cmd).rows("2").attr("readonly", "readonly").style("width:100%; font-size: 10px;")));

		info.add(h3("HTTP API for Deployment:"));

		info.add(grid(apisInfo()));

		return multi(info);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, ?>> apisInfo() {
		return U.list(
			apiInfo("Staging of the application JAR",
				verb(HttpVerb.POST), "/rapidoid/stage",
				U.map("_token", "Deployment token", "file", "<your-jar>"),
				"curl -F 'file=@app.jar' 'http://example.com/rapidoid/stage?_token=..."),

			apiInfo("Deployment of the staged JAR",
				verb(HttpVerb.POST), "/rapidoid/deploy",
				U.map("_token", "Deployment token"),
				"curl -X POST 'http://example.com/rapidoid/deploy?_token=...")
		);
	}

	private Map<String, ?> apiInfo(String desc, Tag verb, String uri, Map<String, String> params, String example) {
		return U.map("description", desc, "verb", verb, "uri", span(uri).class_("text-box"), "parameters", grid(params).headless(true), "example", example);
	}
}
