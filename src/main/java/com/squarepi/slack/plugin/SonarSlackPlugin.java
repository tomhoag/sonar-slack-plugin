package com.squarepi.slack.plugin;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.Plugin;
import org.sonar.api.Plugin.Context;

@Properties({ 
	@Property(key = SonarSlackProperties.ENABLED, 
              name = "Enabled",
              defaultValue = "true",
              description = "If set to false, Slack notifications are not sent",
              global = true,
              project = true,
              type = PropertyType.BOOLEAN),
	@Property(key = SonarSlackProperties.CHANNEL,
              name = "Slack Channel",
              defaultValue = "",
              description = "The Slack channel to send notifications to (e.g. #channel or @username). If left empty, the channel configured with the webhook will be used.  Note: The notification will not be sent if the channel or user does not exist.",
              global = true,
              project = true,
              type = PropertyType.STRING),
    @Property(key = SonarSlackProperties.HANDLE,
              name = "Slack Handle",
              defaultValue = "SonarQube",
              description = "The Slack handle from which this notification is sent.",
              global = true,
              project = true,
              type = PropertyType.STRING),
    @Property(key = SonarSlackProperties.WEBHOOK,
    		  name = "Slack Web Hook",
    		  defaultValue = "",
    		  description = "The Slack web hook used to send notifications",
    		  global = true,
    		  project = true,
    		  type = PropertyType.STRING),    
	@Property(key = SonarSlackProperties.MESSAGE_TEMPLATE,
			  name = "Notification Message Template",
			  description = "The notification message template. For a list of available variables see http://github.com/tomhoag",
			  defaultValue = "$name analyzed at $date $nl Issues: $total (new: $new) $nl Blockers: $blockers (new: $newBlockers) $nl Code Smells: $codeSmells (new: $newCodeSmells)",
			  global = true,
			  project = true,
			  type = PropertyType.STRING),
	})
	
public class SonarSlackPlugin implements Plugin { 
	@Override
	public void define(Context context) {
		context.addExtensions(SonarQualitySlackNotifier.class, SonarIssuesSlackNotifier.class);
	}
}
