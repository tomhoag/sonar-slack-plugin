package com.squarepi.slack.plugin;

import static com.squarepi.slack.plugin.SonarSlackProperties.ENABLED;
import static com.squarepi.slack.plugin.SonarSlackProperties.HANDLE;
import static com.squarepi.slack.plugin.SonarSlackProperties.CHANNEL;
import static com.squarepi.slack.plugin.SonarSlackProperties.WEBHOOK;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

public class SonarSlackNotifier implements PostJob {
	private static final Logger LOGGER = Loggers.get(SonarSlackNotifier.class);

	private Settings settings;
	private ProjectIssues projectIssues;

	public SonarSlackNotifier(Settings settings, ProjectIssues projectIssues) {
		this.settings = settings;
		this.projectIssues = projectIssues;
	}

	@Override
	public void executeOn(Project project, SensorContext context) {

		if (settings.getBoolean(ENABLED)) {
			String channel = settings.getString(CHANNEL);
			String hook = settings.getString(WEBHOOK);
			String handle = settings.getString(HANDLE);
			
			if (isBlank(hook)) {
				LOGGER.warn("No Slack webhook available. Slack notification has not been sent.");
				return;
			}
			
			SonarSlackMessageBuilder messageBuilder = new SonarSlackMessageBuilder(project, settings, projectIssues);
			String statusMessage = messageBuilder.getStatusMessage();
			
			SlackApi api = new SlackApi(hook);
			api.call(new SlackMessage(channel, handle, statusMessage));
		}
	}
}