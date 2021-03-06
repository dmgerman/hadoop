begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PathParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|QueryParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|WebApplicationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|UriInfo
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
name|conf
operator|.
name|Configuration
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
name|http
operator|.
name|JettyUtils
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
name|mapreduce
operator|.
name|JobACL
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|AMInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobState
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Task
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|TaskAttempt
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|AMWebServices
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ConfInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobCounterInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobTaskAttemptCounterInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobTaskCounterInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|MapTaskAttemptInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ReduceTaskAttemptInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|TaskAttemptInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|TaskAttemptsInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|TaskInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|TasksInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HistoryContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AMAttemptInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AMAttemptsInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|HistoryInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobsInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|security
operator|.
name|UserGroupInformation
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|webapp
operator|.
name|BadRequestException
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
name|webapp
operator|.
name|NotFoundException
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
name|webapp
operator|.
name|WebApp
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|Path
argument_list|(
literal|"/ws/v1/history"
argument_list|)
DECL|class|HsWebServices
specifier|public
class|class
name|HsWebServices
block|{
DECL|field|ctx
specifier|private
specifier|final
name|HistoryContext
name|ctx
decl_stmt|;
DECL|field|webapp
specifier|private
name|WebApp
name|webapp
decl_stmt|;
DECL|field|response
specifier|private
annotation|@
name|Context
name|HttpServletResponse
name|response
decl_stmt|;
annotation|@
name|Context
DECL|field|uriInfo
name|UriInfo
name|uriInfo
decl_stmt|;
annotation|@
name|Inject
DECL|method|HsWebServices (final HistoryContext ctx, final Configuration conf, final WebApp webapp)
specifier|public
name|HsWebServices
parameter_list|(
specifier|final
name|HistoryContext
name|ctx
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|WebApp
name|webapp
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|webapp
operator|=
name|webapp
expr_stmt|;
block|}
DECL|method|hasAccess (Job job, HttpServletRequest request)
specifier|private
name|boolean
name|hasAccess
parameter_list|(
name|Job
name|job
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|remoteUser
init|=
name|request
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
return|return
name|job
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|checkAccess (Job job, HttpServletRequest request)
specifier|private
name|void
name|checkAccess
parameter_list|(
name|Job
name|job
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasAccess
argument_list|(
name|job
argument_list|,
name|request
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|WebApplicationException
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|)
throw|;
block|}
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
block|{
comment|//clear content type
name|response
operator|.
name|setContentType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setResponse (HttpServletResponse response)
name|void
name|setResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|get ()
specifier|public
name|HistoryInfo
name|get
parameter_list|()
block|{
return|return
name|getHistoryInfo
argument_list|()
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/info"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getHistoryInfo ()
specifier|public
name|HistoryInfo
name|getHistoryInfo
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
operator|new
name|HistoryInfo
argument_list|()
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobs (@ueryParamR) String userQuery, @QueryParam(R) String count, @QueryParam(R) String stateQuery, @QueryParam(R) String queueQuery, @QueryParam(R) String startedBegin, @QueryParam(R) String startedEnd, @QueryParam(R) String finishBegin, @QueryParam(R) String finishEnd)
specifier|public
name|JobsInfo
name|getJobs
parameter_list|(
annotation|@
name|QueryParam
argument_list|(
literal|"user"
argument_list|)
name|String
name|userQuery
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"limit"
argument_list|)
name|String
name|count
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"state"
argument_list|)
name|String
name|stateQuery
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"queue"
argument_list|)
name|String
name|queueQuery
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"startedTimeBegin"
argument_list|)
name|String
name|startedBegin
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"startedTimeEnd"
argument_list|)
name|String
name|startedEnd
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"finishedTimeBegin"
argument_list|)
name|String
name|finishBegin
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"finishedTimeEnd"
argument_list|)
name|String
name|finishEnd
parameter_list|)
block|{
name|Long
name|countParam
init|=
literal|null
decl_stmt|;
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
operator|&&
operator|!
name|count
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|countParam
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|countParam
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"limit value must be greater then 0"
argument_list|)
throw|;
block|}
block|}
name|Long
name|sBegin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|startedBegin
operator|!=
literal|null
operator|&&
operator|!
name|startedBegin
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|sBegin
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|startedBegin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid number format: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|sBegin
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"startedTimeBegin must be greater than 0"
argument_list|)
throw|;
block|}
block|}
name|Long
name|sEnd
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|startedEnd
operator|!=
literal|null
operator|&&
operator|!
name|startedEnd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|sEnd
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|startedEnd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid number format: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|sEnd
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"startedTimeEnd must be greater than 0"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|sBegin
operator|!=
literal|null
operator|&&
name|sEnd
operator|!=
literal|null
operator|&&
name|sBegin
operator|>
name|sEnd
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"startedTimeEnd must be greater than startTimeBegin"
argument_list|)
throw|;
block|}
name|Long
name|fBegin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|finishBegin
operator|!=
literal|null
operator|&&
operator|!
name|finishBegin
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|fBegin
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|finishBegin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid number format: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|fBegin
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"finishedTimeBegin must be greater than 0"
argument_list|)
throw|;
block|}
block|}
name|Long
name|fEnd
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|finishEnd
operator|!=
literal|null
operator|&&
operator|!
name|finishEnd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|fEnd
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|finishEnd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid number format: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|fEnd
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"finishedTimeEnd must be greater than 0"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fBegin
operator|!=
literal|null
operator|&&
name|fEnd
operator|!=
literal|null
operator|&&
name|fBegin
operator|>
name|fEnd
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"finishedTimeEnd must be greater than finishedTimeBegin"
argument_list|)
throw|;
block|}
name|JobState
name|jobState
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stateQuery
operator|!=
literal|null
condition|)
block|{
name|jobState
operator|=
name|JobState
operator|.
name|valueOf
argument_list|(
name|stateQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|getPartialJobs
argument_list|(
literal|0l
argument_list|,
name|countParam
argument_list|,
name|userQuery
argument_list|,
name|queueQuery
argument_list|,
name|sBegin
argument_list|,
name|sEnd
argument_list|,
name|fBegin
argument_list|,
name|fEnd
argument_list|,
name|jobState
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJob (@ontext HttpServletRequest hsr, @PathParam(R) String jid)
specifier|public
name|JobInfo
name|getJob
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
return|return
operator|new
name|JobInfo
argument_list|(
name|job
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/jobattempts"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobAttempts (@athParamR) String jid)
specifier|public
name|AMAttemptsInfo
name|getJobAttempts
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|AMAttemptsInfo
name|amAttempts
init|=
operator|new
name|AMAttemptsInfo
argument_list|()
decl_stmt|;
for|for
control|(
name|AMInfo
name|amInfo
range|:
name|job
operator|.
name|getAMInfos
argument_list|()
control|)
block|{
name|AMAttemptInfo
name|attempt
init|=
operator|new
name|AMAttemptInfo
argument_list|(
name|amInfo
argument_list|,
name|MRApps
operator|.
name|toString
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|,
name|uriInfo
operator|.
name|getBaseUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|webapp
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|amAttempts
operator|.
name|add
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
block|}
return|return
name|amAttempts
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/counters"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobCounters (@ontext HttpServletRequest hsr, @PathParam(R) String jid)
specifier|public
name|JobCounterInfo
name|getJobCounters
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
return|return
operator|new
name|JobCounterInfo
argument_list|(
name|this
operator|.
name|ctx
argument_list|,
name|job
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/conf"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobConf (@ontext HttpServletRequest hsr, @PathParam(R) String jid)
specifier|public
name|ConfInfo
name|getJobConf
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|ConfInfo
name|info
decl_stmt|;
try|try
block|{
name|info
operator|=
operator|new
name|ConfInfo
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"unable to load configuration for job: "
operator|+
name|jid
argument_list|)
throw|;
block|}
return|return
name|info
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobTasks (@ontext HttpServletRequest hsr, @PathParam(R) String jid, @QueryParam(R) String type)
specifier|public
name|TasksInfo
name|getJobTasks
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"type"
argument_list|)
name|String
name|type
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|TasksInfo
name|allTasks
init|=
operator|new
name|TasksInfo
argument_list|()
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskType
name|ttype
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|ttype
operator|=
name|MRApps
operator|.
name|taskType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"tasktype must be either m or r"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ttype
operator|!=
literal|null
operator|&&
name|task
operator|.
name|getType
argument_list|()
operator|!=
name|ttype
condition|)
block|{
continue|continue;
block|}
name|allTasks
operator|.
name|add
argument_list|(
operator|new
name|TaskInfo
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allTasks
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks/{taskid}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobTask (@ontext HttpServletRequest hsr, @PathParam(R) String jid, @PathParam(R) String tid)
specifier|public
name|TaskInfo
name|getJobTask
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"taskid"
argument_list|)
name|String
name|tid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|AMWebServices
operator|.
name|getTaskFromTaskIdString
argument_list|(
name|tid
argument_list|,
name|job
argument_list|)
decl_stmt|;
return|return
operator|new
name|TaskInfo
argument_list|(
name|task
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks/{taskid}/counters"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getSingleTaskCounters ( @ontext HttpServletRequest hsr, @PathParam(R) String jid, @PathParam(R) String tid)
specifier|public
name|JobTaskCounterInfo
name|getSingleTaskCounters
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"taskid"
argument_list|)
name|String
name|tid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|TaskId
name|taskID
init|=
name|MRApps
operator|.
name|toTaskID
argument_list|(
name|tid
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"taskid "
operator|+
name|tid
operator|+
literal|" not found or invalid"
argument_list|)
throw|;
block|}
name|Task
name|task
init|=
name|job
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"task not found with id "
operator|+
name|tid
argument_list|)
throw|;
block|}
return|return
operator|new
name|JobTaskCounterInfo
argument_list|(
name|task
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobTaskAttempts (@ontext HttpServletRequest hsr, @PathParam(R) String jid, @PathParam(R) String tid)
specifier|public
name|TaskAttemptsInfo
name|getJobTaskAttempts
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"taskid"
argument_list|)
name|String
name|tid
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|TaskAttemptsInfo
name|attempts
init|=
operator|new
name|TaskAttemptsInfo
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|AMWebServices
operator|.
name|getTaskFromTaskIdString
argument_list|(
name|tid
argument_list|,
name|job
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|ta
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|ta
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|attempts
operator|.
name|add
argument_list|(
operator|new
name|ReduceTaskAttemptInfo
argument_list|(
name|ta
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attempts
operator|.
name|add
argument_list|(
operator|new
name|MapTaskAttemptInfo
argument_list|(
name|ta
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|attempts
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts/{attemptid}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobTaskAttemptId (@ontext HttpServletRequest hsr, @PathParam(R) String jid, @PathParam(R) String tid, @PathParam(R) String attId)
specifier|public
name|TaskAttemptInfo
name|getJobTaskAttemptId
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"taskid"
argument_list|)
name|String
name|tid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"attemptid"
argument_list|)
name|String
name|attId
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|AMWebServices
operator|.
name|getTaskFromTaskIdString
argument_list|(
name|tid
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|TaskAttempt
name|ta
init|=
name|AMWebServices
operator|.
name|getTaskAttemptFromTaskAttemptString
argument_list|(
name|attId
argument_list|,
name|task
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
return|return
operator|new
name|ReduceTaskAttemptInfo
argument_list|(
name|ta
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MapTaskAttemptInfo
argument_list|(
name|ta
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts/{attemptid}/counters"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|,
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
block|}
argument_list|)
DECL|method|getJobTaskAttemptIdCounters ( @ontext HttpServletRequest hsr, @PathParam(R) String jid, @PathParam(R) String tid, @PathParam(R) String attId)
specifier|public
name|JobTaskAttemptCounterInfo
name|getJobTaskAttemptIdCounters
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"jobid"
argument_list|)
name|String
name|jid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"taskid"
argument_list|)
name|String
name|tid
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"attemptid"
argument_list|)
name|String
name|attId
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|AMWebServices
operator|.
name|getJobFromJobIdString
argument_list|(
name|jid
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|job
argument_list|,
name|hsr
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|AMWebServices
operator|.
name|getTaskFromTaskIdString
argument_list|(
name|tid
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|TaskAttempt
name|ta
init|=
name|AMWebServices
operator|.
name|getTaskAttemptFromTaskAttemptString
argument_list|(
name|attId
argument_list|,
name|task
argument_list|)
decl_stmt|;
return|return
operator|new
name|JobTaskAttemptCounterInfo
argument_list|(
name|ta
argument_list|)
return|;
block|}
block|}
end_class

end_unit

