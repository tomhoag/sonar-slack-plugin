package com.squarepi.slack.plugin;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.Severity;

import com.google.common.collect.Lists;

public class SonarSlackMessageBuilder {
	private final Project project;
	private final ProjectIssues projectIssues;
	private final Settings settings;

	public SonarSlackMessageBuilder(Project project, Settings settings, ProjectIssues projectIssues) {
		this.project = project;
		this.projectIssues = projectIssues;
		this.settings = settings;
	}

	public String getStatusMessage() {
		List<Issue> issues = Lists.newArrayList(projectIssues.issues());
		
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
				
		List<Issue> issuesResolved = Lists.newArrayList(projectIssues.resolvedIssues());

		Velocity.init();

		VelocityContext context = new VelocityContext();
		context.put("name", project.getName());
		context.put("date", project.getAnalysisDate().toString());
		context.put("new", new Long(issuesNew));
		context.put("resolved", issuesResolved.size());
		context.put("total", issues.size());
		context.put("newBlockers", new Long(issuesNewBlockers));
		context.put("blockers", new Long(issuesBlockers));
		context.put("newCritical", new Long(issuesNewCritical));
		context.put("critical", new Long(issuesCritical));
		context.put("newMajor", new Long(issuesNewMajor));
		context.put("major", new Long(issuesMajor));
		context.put("newMinor", new Long(issuesNewMinor));
		context.put("minor", new Long(issuesMinor));
		context.put("newInfo", new Long(issuesNewInfo));
		context.put("info", new Long(issuesInfo));
		context.put("newCodeSmells", new Long(codeSmellsNew));
		context.put("codeSmells", new Long(codeSmells));
		context.put("newline", "\n");
		context.put("nl", "\n");

		String template = settings.getString(SonarSlackProperties.MESSAGE_TEMPLATE);
		StringWriter writer = new StringWriter();
		Velocity.evaluate(context, writer, "TemplateName", template);

		return writer.toString();
	}
}
