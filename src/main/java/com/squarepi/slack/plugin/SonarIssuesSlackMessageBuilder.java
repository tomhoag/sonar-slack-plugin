package com.squarepi.slack.plugin;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

import org.sonar.api.batch.postjob.issue.PostJobIssue;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.config.Settings;

import com.google.common.collect.Lists;

public class SonarIssuesSlackMessageBuilder {
	private final Settings settings;
	private final PostJobContext context;

	public SonarIssuesSlackMessageBuilder(Settings settings, PostJobContext context) {
		this.settings = settings;
		this.context = context;
	}

	public String getStatusMessage() {
		List<PostJobIssue> issues = Lists.newArrayList(context.issues());
		
		long issuesNew = issues.stream().filter(i -> i.isNew()).count();

		long issuesNewBlockers = issues.stream().filter(i -> i.isNew() && i.severity().equals(Severity.BLOCKER)).count();
		long issuesBlockers = issues.stream().filter(i -> i.severity().equals(Severity.BLOCKER)).count();
		
		long issuesNewCritical = issues.stream().filter(i -> i.isNew() && i.severity().equals(Severity.CRITICAL)).count();
		long issuesCritical = issues.stream().filter(i -> i.severity().equals(Severity.CRITICAL)).count();
		
		long issuesNewMajor = issues.stream().filter(i -> i.isNew() && i.severity().equals(Severity.MAJOR)).count();
		long issuesMajor = issues.stream().filter(i -> i.severity().equals(Severity.MAJOR)).count();
		
		long issuesNewMinor = issues.stream().filter(i -> i.isNew() && i.severity().equals(Severity.MINOR)).count();
		long issuesMinor = issues.stream().filter(i -> i.severity().equals(Severity.MINOR)).count();
		
		long issuesNewInfo = issues.stream().filter(i -> i.isNew() && i.severity().equals(Severity.INFO)).count();
		long issuesInfo = issues.stream().filter(i -> i.severity().equals(Severity.INFO)).count();
		
		long codeSmellsNew = issuesNewCritical + issuesNewMajor + issuesNewMinor + issuesNewInfo;
		long codeSmells = issuesCritical + issuesMajor + issuesMinor + issuesInfo;
				
		Velocity.init();

		VelocityContext velocityContext = new VelocityContext();
		
		velocityContext.put("name", settings.getString("sonar.projectKey"));
		velocityContext.put("date", DateFormat.getDateInstance().format(new Date()));
		velocityContext.put("new", new Long(issuesNew));

		velocityContext.put("total", issues.size());
		velocityContext.put("newBlockers", new Long(issuesNewBlockers));
		velocityContext.put("blockers", new Long(issuesBlockers));
		velocityContext.put("newCritical", new Long(issuesNewCritical));
		velocityContext.put("critical", new Long(issuesCritical));
		velocityContext.put("newMajor", new Long(issuesNewMajor));
		velocityContext.put("major", new Long(issuesMajor));
		velocityContext.put("newMinor", new Long(issuesNewMinor));
		velocityContext.put("minor", new Long(issuesMinor));
		velocityContext.put("newInfo", new Long(issuesNewInfo));
		velocityContext.put("info", new Long(issuesInfo));
		velocityContext.put("newCodeSmells", new Long(codeSmellsNew));
		velocityContext.put("codeSmells", new Long(codeSmells));
		velocityContext.put("newline", "\n");

		velocityContext.put("nl", "\n");

		String template = settings.getString(SonarSlackProperties.MESSAGE_TEMPLATE);
		StringWriter writer = new StringWriter();
		Velocity.evaluate(velocityContext, writer, "TemplateName", template);

		return writer.toString();
	}
}
