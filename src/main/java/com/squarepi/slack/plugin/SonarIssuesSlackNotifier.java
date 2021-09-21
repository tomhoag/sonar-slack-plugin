package com.squarepi.slack.plugin;

import static com.squarepi.slack.plugin.SonarSlackProperties.ENABLED;
import static com.squarepi.slack.plugin.SonarSlackProperties.HANDLE;
import static com.squarepi.slack.plugin.SonarSlackProperties.CHANNEL;
import static com.squarepi.slack.plugin.SonarSlackProperties.WEBHOOK;

import org.sonar.api.batch.postjob.PostJob;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.postjob.PostJobDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static org.apache.commons.lang.StringUtils.isBlank;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;


public class SonarIssuesSlackNotifier implements PostJob {

	private static final Logger LOGGER = Loggers.get(SonarIssuesSlackNotifier.class);

	private final Configuration config;

	public SonarIssuesSlackNotifier(Configuration config) {
		this.config = config;
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

			if (config.getBoolean(ENABLED).isPresent() && config.getBoolean(ENABLED).get()) {
				String channel = config.get(CHANNEL).orElse(null);
				String hook = config.get(WEBHOOK).orElse(null);
				String handle = config.get(HANDLE).orElse(null);

				if (isBlank(hook)) {
					LOGGER.warn("No Slack webhook available. Slack notification has not been sent.");
					return;
				}

				SonarIssuesSlackMessageBuilder messageBuilder = new SonarIssuesSlackMessageBuilder(config, context);
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
