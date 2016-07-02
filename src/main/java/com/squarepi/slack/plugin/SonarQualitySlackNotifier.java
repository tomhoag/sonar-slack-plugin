package com.squarepi.slack.plugin;

import java.lang.StringBuilder;

import static org.sonar.api.CoreProperties.PROJECT_KEY_PROPERTY;
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
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.QualityGate;

import static org.sonar.api.ce.posttask.QualityGate.Status.ERROR;
import static org.sonar.api.ce.posttask.QualityGate.Status.OK; 
import static org.sonar.api.ce.posttask.QualityGate.Status.WARN; 

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;


public class SonarQualitySlackNotifier implements PostProjectAnalysisTask {

	private Settings settings;

	public SonarQualitySlackNotifier(Settings settings) {
		this.settings = settings;
	}
	
	@Override
	public void finished(ProjectAnalysis analysis) {
	
		QualityGate gate = analysis.getQualityGate();
		
		if (gate != null && settings.getBoolean(ENABLED)) {
		
			String channel = settings.getString(CHANNEL);
			String hook = settings.getString(WEBHOOK);
			String handle = settings.getString(HANDLE);
			
			StringBuilder statusMessage = new StringBuilder(settings.getString(PROJECT_KEY_PROPERTY) + ": Quality gate is " + gate.getStatus());
			
			if(gate.getStatus() == ERROR) {
				statusMessage.append(" :white_frowning_face:");
			} else if(gate.getStatus() == WARN) {
				statusMessage.append(" :confused:");
			} else { // OK
				statusMessage.append(" :smile:");
			}
			
			SlackApi api = new SlackApi(hook);
			api.call(new SlackMessage(channel, handle, statusMessage.toString()));
			
			Loggers.get(getClass()).info("Quality gate is " + gate.getStatus());
			Loggers.get(getClass()).info("project name is " + settings.getString(PROJECT_KEY_PROPERTY));
		} else {
			Loggers.get(getClass()).info("gate: " + gate);
			Loggers.get(getClass()).info("enabled: " + settings.getBoolean(ENABLED));
		}
	}
}