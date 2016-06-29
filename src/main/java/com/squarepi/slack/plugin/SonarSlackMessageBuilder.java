package com.squarepi.slack.plugin;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.batch.postjob.issue.PostJobIssue;
import org.sonar.api.batch.postjob.PostJobContext;

import org.sonar.api.resources.Project;
import org.sonar.api.batch.rule.Severity;

import com.google.common.collect.Lists;

public class SonarSlackMessageBuilder {
	//private final Project project;
	//private final ProjectIssues projectIssues;
	private final Settings settings;
	private final PostJobContext context;

	public SonarSlackMessageBuilder(Settings settings, PostJobContext context) {
		//this.projectIssues = projectIssues;
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
				
		//List<Issue> issuesResolved = Lists.newArrayList(projectIssues.resolvedIssues());

		Velocity.init();

		VelocityContext velocityContext = new VelocityContext();
		//velocityContext.put("name", project.getName());
		//velocityContext.put("date", project.getAnalysisDate().toString());
		velocityContext.put("new", new Long(issuesNew));
		//velocityContext.put("resolved", issuesResolved.size());

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
