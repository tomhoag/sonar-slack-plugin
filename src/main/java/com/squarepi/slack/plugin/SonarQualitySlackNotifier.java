package com.squarepi.slack.plugin;

import static com.squarepi.slack.plugin.SonarSlackProperties.ENABLED;
import static com.squarepi.slack.plugin.SonarSlackProperties.HANDLE;
import static com.squarepi.slack.plugin.SonarSlackProperties.CHANNEL;
import static com.squarepi.slack.plugin.SonarSlackProperties.WEBHOOK;

import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.QualityGate;

import static org.sonar.api.ce.posttask.QualityGate.Status.ERROR;
import static org.sonar.api.ce.posttask.QualityGate.Status.WARN;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;


public class SonarQualitySlackNotifier implements PostProjectAnalysisTask {

	private final Configuration config;

	public SonarQualitySlackNotifier(Configuration config) {
		this.config = config;
	}

	@Override
	public void finished(ProjectAnalysis analysis) {

		QualityGate gate = analysis.getQualityGate();

		if (gate != null && config.getBoolean(ENABLED).isPresent() && config.getBoolean(ENABLED).get()) {

			String channel = config.get(CHANNEL).orElse(null);
			String hook = config.get(WEBHOOK).orElse(null);
			String handles = config.get(HANDLE).orElse(null);

			StringBuilder statusMessage = new StringBuilder(analysis.getProject().getKey() + ": Quality gate is " + gate.getStatus());

			if(gate.getStatus() == ERROR) {
				statusMessage.append(" :white_frowning_face:");
			} else if(gate.getStatus() == WARN) {
				statusMessage.append(" :confused:");
			} else { // OK
				statusMessage.append(" :smile:");
			}

			if(handles != null) {
				SlackApi api = new SlackApi(hook);
				for(String handle: handles.split(",")) {
					if(handle != null) {
						api.call(new SlackMessage(channel, handle.trim(), statusMessage.toString()));
					} else {
						Loggers.get(getClass()).info("statusMessage not sent to " + handle);
					}
				}
			}

			Loggers.get(getClass()).info(statusMessage.toString());
		} else {
			Loggers.get(getClass()).info("enabled: " + config.getBoolean(ENABLED));
		}
	}
}
