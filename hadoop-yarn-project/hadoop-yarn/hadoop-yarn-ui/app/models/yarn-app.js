import Converter from 'yarn-ui/utils/converter';
import DS from 'ember-data';

export default DS.Model.extend({
	appName: DS.attr('string'),
	user: DS.attr('string'),
	queue: DS.attr('string'),
	state: DS.attr('string'),
	startTime: DS.attr('string'),
	elapsedTime: DS.attr('string'),
  finalStatus: DS.attr('string'),
  finishedTime: DS.attr('finishedTime'),
  progress: DS.attr('number'),
  diagnostics: DS.attr('string'),
  amContainerLogs: DS.attr('string'),
  amHostHttpAddress: DS.attr('string'),
  logAggregationStatus: DS.attr('string'),
  unmanagedApplication: DS.attr('string'),
  amNodeLabelExpression: DS.attr('string'),
  applicationTags: DS.attr('string'),
  priority: DS.attr('number'),
  allocatedMB: DS.attr('number'),
  allocatedVCores: DS.attr('number'),
  runningContainers: DS.attr('number'),
  memorySeconds: DS.attr('number'),
  vcoreSeconds: DS.attr('number'),
  preemptedResourceMB: DS.attr('number'),
  preemptedResourceVCores: DS.attr('number'),
  numNonAMContainerPreempted: DS.attr('number'),
  numAMContainerPreempted: DS.attr('number'),

  isFailed: function() {
    return this.get('finalStatus') == "FAILED"
  }.property("finalStatus"),

  allocatedResource: function() {
    return Converter.resourceToString(this.get("allocatedMB"), this.get("allocatedVCores"));
  }.property("allocatedMB", "allocatedVCores"),

  preemptedResource: function() {
    return Converter.resourceToString(this.get("preemptedResourceMB"), this.get("preemptedResourceVCores"));
  }.property("preemptedResourceMB", "preemptedResourceVCores"),

  aggregatedResourceUsage: function() {
    return Converter.resourceToString(this.get("memorySeconds"), this.get("vcoreSeconds")) + " (× Secs)";
  }.property("memorySeconds", "vcoreSeconds"),

  progressStyle: function() {
    return "width: " + this.get("progress") + "%";
  }.property("progress"),

  finalStatusStyle: function() {
    var style = "default";
    var finalStatus = this.get("finalStatus");
    if (finalStatus == "KILLED") {
      style = "warning";
    } else if (finalStatus == "FAILED") {
      style = "danger";
    } else {
      style = "success";
    }

    return "label label-" + style;
  }.property("finalStatus")
});