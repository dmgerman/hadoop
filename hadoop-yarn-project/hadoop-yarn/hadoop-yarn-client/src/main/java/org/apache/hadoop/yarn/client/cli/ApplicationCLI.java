begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|GnuParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|HelpFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
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
name|util
operator|.
name|ToolRunner
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
name|ApplicationId
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
name|ApplicationReport
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
name|YarnRemoteException
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
name|util
operator|.
name|ConverterUtils
import|;
end_import

begin_class
DECL|class|ApplicationCLI
specifier|public
class|class
name|ApplicationCLI
extends|extends
name|YarnCLI
block|{
DECL|field|APPLICATIONS_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATIONS_PATTERN
init|=
literal|"%30s\t%20s\t%10s\t%10s\t%18s\t%18s\t%35s\n"
decl_stmt|;
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationCLI
name|cli
init|=
operator|new
name|ApplicationCLI
argument_list|()
decl_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|cli
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|cli
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|STATUS_CMD
argument_list|,
literal|true
argument_list|,
literal|"Prints the status of the application."
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|LIST_CMD
argument_list|,
literal|false
argument_list|,
literal|"Lists all the Applications from RM."
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|KILL_CMD
argument_list|,
literal|true
argument_list|,
literal|"Kills the application."
argument_list|)
expr_stmt|;
name|CommandLine
name|cliParser
init|=
operator|new
name|GnuParser
argument_list|()
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
name|STATUS_CMD
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
name|printApplicationReport
argument_list|(
name|cliParser
operator|.
name|getOptionValue
argument_list|(
name|STATUS_CMD
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
name|LIST_CMD
argument_list|)
condition|)
block|{
name|listAllApplications
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
name|KILL_CMD
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
name|killApplication
argument_list|(
name|cliParser
operator|.
name|getOptionValue
argument_list|(
name|KILL_CMD
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|syserr
operator|.
name|println
argument_list|(
literal|"Invalid Command Usage : "
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * It prints the usage of the command    *     * @param opts    */
DECL|method|printUsage (Options opts)
specifier|private
name|void
name|printUsage
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"application"
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
comment|/**    * Lists all the applications present in the Resource Manager    *     * @throws YarnRemoteException    */
DECL|method|listAllApplications ()
specifier|private
name|void
name|listAllApplications
parameter_list|()
throws|throws
name|YarnRemoteException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|sysout
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|appsReport
init|=
name|client
operator|.
name|getApplicationList
argument_list|()
decl_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Total Applications:"
operator|+
name|appsReport
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|printf
argument_list|(
name|APPLICATIONS_PATTERN
argument_list|,
literal|"Application-Id"
argument_list|,
literal|"Application-Name"
argument_list|,
literal|"User"
argument_list|,
literal|"Queue"
argument_list|,
literal|"State"
argument_list|,
literal|"Final-State"
argument_list|,
literal|"Tracking-URL"
argument_list|)
expr_stmt|;
for|for
control|(
name|ApplicationReport
name|appReport
range|:
name|appsReport
control|)
block|{
name|writer
operator|.
name|printf
argument_list|(
name|APPLICATIONS_PATTERN
argument_list|,
name|appReport
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appReport
operator|.
name|getName
argument_list|()
argument_list|,
name|appReport
operator|.
name|getUser
argument_list|()
argument_list|,
name|appReport
operator|.
name|getQueue
argument_list|()
argument_list|,
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|,
name|appReport
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|,
name|appReport
operator|.
name|getOriginalTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Kills the application with the application id as appId    *     * @param applicationId    * @throws YarnRemoteException    */
DECL|method|killApplication (String applicationId)
specifier|private
name|void
name|killApplication
parameter_list|(
name|String
name|applicationId
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationId
name|appId
init|=
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
name|sysout
operator|.
name|println
argument_list|(
literal|"Killing application "
operator|+
name|applicationId
argument_list|)
expr_stmt|;
name|client
operator|.
name|killApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Prints the application report for an application id.    *     * @param applicationId    * @throws YarnRemoteException    */
DECL|method|printApplicationReport (String applicationId)
specifier|private
name|void
name|printApplicationReport
parameter_list|(
name|String
name|applicationId
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationReport
name|appReport
init|=
name|client
operator|.
name|getApplicationReport
argument_list|(
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|applicationId
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuffer
name|appReportStr
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|appReport
operator|!=
literal|null
condition|)
block|{
name|appReportStr
operator|.
name|append
argument_list|(
literal|"Application Report : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tApplication-Id : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tApplication-Name : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tUser : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tQueue : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tStart-Time : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tFinish-Time : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tState : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tFinal-State : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tTracking-URL : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getOriginalTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
literal|"\n\tDiagnostics : "
argument_list|)
expr_stmt|;
name|appReportStr
operator|.
name|append
argument_list|(
name|appReport
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appReportStr
operator|.
name|append
argument_list|(
literal|"Application with id '"
operator|+
name|applicationId
operator|+
literal|"' doesn't exist in RM."
argument_list|)
expr_stmt|;
block|}
name|sysout
operator|.
name|println
argument_list|(
name|appReportStr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

