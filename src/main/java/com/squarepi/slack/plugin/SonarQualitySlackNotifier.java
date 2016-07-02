package com.squarepi.slack.plugin;

import static org.sonar.api.CoreProperties.PROJECT_KEY_PROPERTY;
import static com.squarepi.slack.plugin.SonarSlackProperties.ENABLED;
import static com.squarepi.slack.plugin.SonarSlackProperties.HANDLE;
import static com.squarepi.slack.plugin.SonarSlackProperties.CHANNEL;
import static com.squarepi.slack.plugin.SonarSlackProperties.WEBHOOK;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.sonar.api.config.Settings;
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
			String handles = settings.getString(HANDLE);
			
			StringBuilder statusMessage = new StringBuilder(settings.getString("sonar.projectKey") + ": Quality gate is " + gate.getStatus());
			
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
					if(handle != null && (handle.trim().startsWith("#") || handle.trim().startsWith("@"))) {
						api.call(new SlackMessage(channel, handle.trim(), statusMessage.toString()));
					} else {
						Loggers.get(getClass()).info("statusMessage not sent to " + handle);
					}
				}
			}
					
			Loggers.get(getClass()).info(statusMessage.toString());
		} else {
			Loggers.get(getClass()).info("enabled: " + settings.getBoolean(ENABLED));
		}
	}
}