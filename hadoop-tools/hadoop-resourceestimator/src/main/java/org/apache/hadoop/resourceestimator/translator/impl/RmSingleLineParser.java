begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.translator.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|RecurrenceId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|ResourceSkyline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|api
operator|.
name|JobMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|api
operator|.
name|SingleLineParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|exceptions
operator|.
name|DataFieldNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
import|;
end_import

begin_comment
comment|/**  * {@link SingleLineParser} for Hadoop Resource Manager logs.  */
end_comment

begin_class
DECL|class|RmSingleLineParser
specifier|public
class|class
name|RmSingleLineParser
implements|implements
name|SingleLineParser
block|{
DECL|field|PARSERUTIL
specifier|private
specifier|static
specifier|final
name|LogParserUtil
name|PARSERUTIL
init|=
operator|new
name|LogParserUtil
argument_list|()
decl_stmt|;
DECL|field|FILTER_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|FILTER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(Submit Application Request|AM Allocated Container|"
operator|+
literal|"AM Released Container|finalState=FAILED|"
operator|+
literal|"ApplicationSummary|, Resource:)"
argument_list|)
decl_stmt|;
DECL|field|SUBMISSION_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|SUBMISSION_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"APPID=(\\w+)"
argument_list|)
decl_stmt|;
DECL|field|FAIL_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|FAIL_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"appattempt_(\\d+_\\d+)_\\d+"
argument_list|)
decl_stmt|;
DECL|field|FINISH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|FINISH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"appId=(\\w+).*?name=(\\w+)\\-(\\w+)"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_EVENT_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|CONTAINER_EVENT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"APPID=(\\w+).*?CONTAINERID=(\\w+)"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_SPEC_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|CONTAINER_SPEC_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(container_[^_]+|appattempt)_(\\d+_\\d+).*?memory:(\\d+),"
operator|+
literal|"\\svCores:(\\d+)"
argument_list|)
decl_stmt|;
comment|/**    * Aggregates different jobs' {@link ResourceSkyline}s within the same    * pipeline together.    *    * @param resourceSkyline newly extracted {@link ResourceSkyline}.    * @param recurrenceId    the {@link RecurrenceId} which the resourceSkyline    *                        belongs to.    * @param skylineRecords  a {@link Map} which stores the    *     {@link ResourceSkyline}s for all pipelines during this parsing.    */
DECL|method|aggregateSkyline (final ResourceSkyline resourceSkyline, final RecurrenceId recurrenceId, final Map<RecurrenceId, List<ResourceSkyline>> skylineRecords)
specifier|private
name|void
name|aggregateSkyline
parameter_list|(
specifier|final
name|ResourceSkyline
name|resourceSkyline
parameter_list|,
specifier|final
name|RecurrenceId
name|recurrenceId
parameter_list|,
specifier|final
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|skylineRecords
parameter_list|)
block|{
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
name|resourceSkylines
init|=
name|skylineRecords
operator|.
name|get
argument_list|(
name|recurrenceId
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceSkylines
operator|==
literal|null
condition|)
block|{
name|resourceSkylines
operator|=
operator|new
name|ArrayList
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|()
expr_stmt|;
name|skylineRecords
operator|.
name|put
argument_list|(
name|recurrenceId
argument_list|,
name|resourceSkylines
argument_list|)
expr_stmt|;
block|}
name|resourceSkylines
operator|.
name|add
argument_list|(
name|resourceSkyline
argument_list|)
expr_stmt|;
block|}
DECL|method|parseLine (final String logLine, final Map<String, JobMetaData> jobMetas, final Map<RecurrenceId, List<ResourceSkyline>> skylineRecords)
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|parseLine
parameter_list|(
specifier|final
name|String
name|logLine
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|JobMetaData
argument_list|>
name|jobMetas
parameter_list|,
specifier|final
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|skylineRecords
parameter_list|)
throws|throws
name|DataFieldNotFoundException
throws|,
name|ParseException
block|{
specifier|final
name|String
index|[]
name|splits
init|=
name|logLine
operator|.
name|split
argument_list|(
literal|","
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// Limit the max number of 5
comment|// splits
if|if
condition|(
name|splits
operator|.
name|length
operator|<
literal|5
condition|)
block|{
return|return;
block|}
specifier|final
name|Matcher
name|jobEventMatcher
init|=
name|FILTER_PATTERN
operator|.
name|matcher
argument_list|(
name|splits
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
comment|// search
comment|// only
comment|// the
comment|// tail
if|if
condition|(
operator|!
name|jobEventMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// this line of log does not contain targeted
comment|// events
return|return;
block|}
comment|// now we have the match, let's do some parsing
specifier|final
name|long
name|date
init|=
name|PARSERUTIL
operator|.
name|stringToUnixTimestamp
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tail
init|=
name|splits
index|[
literal|4
index|]
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|,
literal|4
argument_list|)
index|[
literal|3
index|]
decl_stmt|;
comment|// use the tail of the
comment|// tail only
switch|switch
condition|(
name|jobEventMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
literal|"Submit Application Request"
case|:
block|{
comment|/** Submit job. */
specifier|final
name|Matcher
name|appIdMatch
init|=
name|SUBMISSION_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|appIdMatch
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
name|appIdMatch
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|jobMetas
operator|.
name|put
argument_list|(
name|appId
argument_list|,
operator|new
name|JobMetaData
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
literal|"AM Allocated Container"
case|:
block|{
comment|/** Allocate container. */
specifier|final
name|Matcher
name|containerEventMatcher
init|=
name|CONTAINER_EVENT_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerEventMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
name|containerEventMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|containerId
init|=
name|containerEventMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|JobMetaData
name|appMeta
init|=
name|jobMetas
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appMeta
operator|!=
literal|null
condition|)
block|{
name|appMeta
operator|.
name|setContainerStart
argument_list|(
name|containerId
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
literal|", Resource:"
case|:
block|{
specifier|final
name|Matcher
name|containerSpecMatcher
init|=
name|CONTAINER_SPEC_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerSpecMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
literal|"application_"
operator|+
name|containerSpecMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|JobMetaData
name|appMeta
init|=
name|jobMetas
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appMeta
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|memAlloc
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|containerSpecMatcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cpuAlloc
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|containerSpecMatcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|containerAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memAlloc
argument_list|,
name|cpuAlloc
argument_list|)
decl_stmt|;
name|appMeta
operator|.
name|getResourceSkyline
argument_list|()
operator|.
name|setContainerSpec
argument_list|(
name|containerAlloc
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
literal|"AM Released Container"
case|:
block|{
specifier|final
name|Matcher
name|containerEventMatcher
init|=
name|CONTAINER_EVENT_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerEventMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
name|containerEventMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|JobMetaData
name|appMeta
init|=
name|jobMetas
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appMeta
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|containerId
init|=
name|containerEventMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|appMeta
operator|.
name|setContainerEnd
argument_list|(
name|containerId
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
literal|"finalState=FAILED"
case|:
block|{
comment|/** In case of appAttempt failed: discard previous records. */
specifier|final
name|Matcher
name|failMatcher
init|=
name|FAIL_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|failMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
literal|"application_"
operator|+
name|failMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobMetas
operator|.
name|containsKey
argument_list|(
name|appId
argument_list|)
condition|)
block|{
name|jobMetas
operator|.
name|put
argument_list|(
name|appId
argument_list|,
operator|new
name|JobMetaData
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
literal|"ApplicationSummary"
case|:
block|{
comment|/** Finish a job. */
specifier|final
name|Matcher
name|finishMatcher
init|=
name|FINISH_PATTERN
operator|.
name|matcher
argument_list|(
name|tail
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|String
name|appId
init|=
name|finishMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|pipelineId
init|=
name|finishMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|runId
init|=
name|finishMatcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|RecurrenceId
name|recurrenceId
init|=
operator|new
name|RecurrenceId
argument_list|(
name|pipelineId
argument_list|,
name|runId
argument_list|)
decl_stmt|;
specifier|final
name|JobMetaData
name|appMeta
init|=
name|jobMetas
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appMeta
operator|!=
literal|null
condition|)
block|{
name|appMeta
operator|.
name|setRecurrenceId
argument_list|(
name|recurrenceId
argument_list|)
operator|.
name|setJobFinishTime
argument_list|(
name|date
argument_list|)
operator|.
name|getResourceSkyline
argument_list|()
operator|.
name|setJobInputDataSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// TODO: need to
comment|// read job input
comment|// data size from
comment|// logs
name|appMeta
operator|.
name|createSkyline
argument_list|()
expr_stmt|;
specifier|final
name|ResourceSkyline
name|resourceSkyline
init|=
name|appMeta
operator|.
name|getResourceSkyline
argument_list|()
decl_stmt|;
name|resourceSkyline
operator|.
name|setJobId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|aggregateSkyline
argument_list|(
name|resourceSkyline
argument_list|,
name|recurrenceId
argument_list|,
name|skylineRecords
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataFieldNotFoundException
argument_list|(
name|tail
argument_list|)
throw|;
block|}
break|break;
block|}
default|default:
break|break;
block|}
block|}
block|}
end_class

end_unit

