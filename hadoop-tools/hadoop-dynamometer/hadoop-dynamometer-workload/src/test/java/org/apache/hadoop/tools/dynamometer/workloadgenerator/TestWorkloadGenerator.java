begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
operator|.
name|AuditCommandParser
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
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
operator|.
name|AuditLogDirectParser
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
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
operator|.
name|AuditLogHiveTableParser
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
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
operator|.
name|AuditReplayMapper
import|;
end_import

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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|Counters
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
name|security
operator|.
name|authorize
operator|.
name|AuthorizationException
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
name|authorize
operator|.
name|ImpersonationProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_IMPERSONATION_PROVIDER_CLASS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/** Tests for {@link WorkloadDriver} and related classes. */
end_comment

begin_class
DECL|class|TestWorkloadGenerator
specifier|public
class|class
name|TestWorkloadGenerator
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|miniCluster
specifier|private
name|MiniDFSCluster
name|miniCluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|FileSystem
name|dfs
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|HADOOP_SECURITY_IMPERSONATION_PROVIDER_CLASS
argument_list|,
name|AllowUserImpersonationProvider
operator|.
name|class
argument_list|,
name|ImpersonationProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|miniCluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|miniCluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|miniCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setOwner
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
literal|"hdfs"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|miniCluster
operator|!=
literal|null
condition|)
block|{
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|miniCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAuditWorkloadDirectParser ()
specifier|public
name|void
name|testAuditWorkloadDirectParser
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|workloadInputPath
init|=
name|TestWorkloadGenerator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"audit_trace_direct"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AuditReplayMapper
operator|.
name|INPUT_PATH_KEY
argument_list|,
name|workloadInputPath
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|testAuditWorkload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAuditWorkloadHiveParser ()
specifier|public
name|void
name|testAuditWorkloadHiveParser
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|workloadInputPath
init|=
name|TestWorkloadGenerator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"audit_trace_hive"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AuditReplayMapper
operator|.
name|INPUT_PATH_KEY
argument_list|,
name|workloadInputPath
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|AuditReplayMapper
operator|.
name|COMMAND_PARSER_KEY
argument_list|,
name|AuditLogHiveTableParser
operator|.
name|class
argument_list|,
name|AuditCommandParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|testAuditWorkload
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@link ImpersonationProvider} that confirms the user doing the    * impersonating is the same as the user running the MiniCluster.    */
DECL|class|AllowUserImpersonationProvider
specifier|private
specifier|static
class|class
name|AllowUserImpersonationProvider
extends|extends
name|Configured
implements|implements
name|ImpersonationProvider
block|{
DECL|method|init (String configurationPrefix)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|configurationPrefix
parameter_list|)
block|{
comment|// Do nothing
block|}
DECL|method|authorize (UserGroupInformation user, String remoteAddress)
specifier|public
name|void
name|authorize
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|String
name|remoteAddress
parameter_list|)
throws|throws
name|AuthorizationException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|getRealUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|()
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|testAuditWorkload ()
specifier|private
name|void
name|testAuditWorkload
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|workloadStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
decl_stmt|;
name|Job
name|workloadJob
init|=
name|WorkloadDriver
operator|.
name|getJobForSubmission
argument_list|(
name|conf
argument_list|,
name|dfs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|workloadStartTime
argument_list|,
name|AuditReplayMapper
operator|.
name|class
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|workloadJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"workload job should succeed"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|Counters
name|counters
init|=
name|workloadJob
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|AuditReplayMapper
operator|.
name|REPLAYCOUNTERS
operator|.
name|TOTALCOMMANDS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|AuditReplayMapper
operator|.
name|REPLAYCOUNTERS
operator|.
name|TOTALINVALIDCOMMANDS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/test1"
argument_list|)
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/testDirRenamed"
argument_list|)
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/denied"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

