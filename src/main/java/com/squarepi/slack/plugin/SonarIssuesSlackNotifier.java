package com.squarepi.slack.plugin;

import static com.squarepi.slack.plugin.SonarSlackProperties.ENABLED;
import static com.squarepi.slack.plugin.SonarSlackProperties.HANDLE;
import static com.squarepi.slack.plugin.SonarSlackProperties.CHANNEL;
import static com.squarepi.slack.plugin.SonarSlackProperties.WEBHOOK;

import org.sonar.api.batch.postjob.PostJob;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.postjob.PostJobDescriptor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static org.apache.commons.lang.StringUtils.isBlank;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;


public class SonarIssuesSlackNotifier implements PostJob {

	private static final Logger LOGGER = Loggers.get(SonarIssuesSlackNotifier.class);
	
	private Settings settings;
	private SensorContext sensor;
	
	public SonarIssuesSlackNotifier(Settings settings, SensorContext sensor) {
		this.settings = settings;
		this.sensor = sensor;
		LOGGER.info("SonarIssuesSlackNotifier Hello World!!");
	}
	
	@Override
	public void describe(PostJobDescriptor descriptor) {
		descriptor.name("Display issues");
	}

	@Override
	public void execute(PostJobContext context) {
		// issues are not accessible when the mode "issues" is not enabled
		// with the scanner property "sonar.analysis.mode=issues"
				
		if (context.analysisMode().isIssues()) {
			
			if (settings.getBoolean(ENABLED)) {
				String channel = settings.getString(CHANNEL);
				String hook = settings.getString(WEBHOOK);
				String handle = settings.getString(HANDLE);
				
				if (isBlank(hook)) {
					LOGGER.warn("No Slack webhook available. Slack notification has not been sent.");
					return;
				}
				
				SonarIssuesSlackMessageBuilder messageBuilder = new SonarIssuesSlackMessageBuilder(settings, context, sensor);
				String statusMessage = messageBuilder.getStatusMessage();
				
				LOGGER.info("statusMessage: " + statusMessage);
				
				SlackApi api = new SlackApi(hook);
				api.call(new SlackMessage(channel, handle, statusMessage));
			}
		} else {
			LOGGER.info("!context.analysisMode().isIssues()");
		}
	}
}