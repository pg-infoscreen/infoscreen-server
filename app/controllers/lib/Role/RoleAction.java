/**
Copyright 2015 Fabian Bock, Fabian Bruckner, Christine Dahn, Amin Nirazi, Matthäus Poloczek, Kai Sauerwald, Michael Schultz, Shabnam Tabatabaian, Tim Tegeler und Marvin Wepner

This file is part of pg-infoscreen.

pg-infoscreen is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

pg-infoscreen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with pg-infoscreen.  If not, see <http://www.gnu.org/licenses/>.
*/
package controllers.lib.Role;

// Import java classes

// Import play models
import play.Logger;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;

public class RoleAction extends Action<Roles>
{

	public boolean checkChannelPrevileges(Http.Context ctx)
	{
		JsonNode json = ctx.request().body().asJson();

		final int id = json.get("channel_id").asInt();

		return SecuredAction.admin.roles.stream().anyMatch(r -> r.channels.stream().anyMatch(c -> c.channel_id == id));
	}

	public F.Promise<Result> call(Http.Context ctx) throws Throwable
	{
		// Superadmin darf alles
		if (SecuredAction.admin.roles != null && SecuredAction.admin.roles.stream().anyMatch(r -> r.name != null? r.name.equals("SuperAdmin") : false)) {
			return delegate.call(ctx);
		} else if (SecuredAction.admin.roles != null
				&& SecuredAction.admin.roles.stream().anyMatch(role -> Arrays.asList(configuration.value()).contains(role.name))) {
			return delegate.call(ctx);
		} else if (Arrays.asList(configuration.value()).contains("Channel")) {
			if (!checkChannelPrevileges(ctx)) {
				Result result = unauthorized("unauthorized");
				return F.Promise.pure(result);
			}
			return delegate.call(ctx);
		} else {

			Result result = unauthorized("unauthorized");
			return F.Promise.pure(result);
		}
	}
}
