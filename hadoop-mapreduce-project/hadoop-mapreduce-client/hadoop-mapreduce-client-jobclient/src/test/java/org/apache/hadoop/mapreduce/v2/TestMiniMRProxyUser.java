begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
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
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ipc
operator|.
name|RemoteException
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
name|mapred
operator|.
name|JobClient
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|MiniMRCluster
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
name|mapred
operator|.
name|RunningJob
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
name|ProxyUsers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_class
DECL|class|TestMiniMRProxyUser
specifier|public
class|class
name|TestMiniMRProxyUser
extends|extends
name|TestCase
block|{
DECL|field|dfsCluster
specifier|private
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|mrCluster
specifier|private
name|MiniMRCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
block|}
name|int
name|taskTrackers
init|=
literal|2
decl_stmt|;
name|int
name|dataNodes
init|=
literal|2
decl_stmt|;
name|String
name|proxyUser
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|String
name|proxyGroup
init|=
literal|"g"
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"127.0.0.1,localhost"
argument_list|)
expr_stmt|;
for|for
control|(
name|InetAddress
name|i
range|:
name|InetAddress
operator|.
name|getAllByName
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.block.access.token.enable"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.permissions"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser."
operator|+
name|proxyUser
operator|+
literal|".hosts"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser."
operator|+
name|proxyUser
operator|+
literal|".groups"
argument_list|,
name|proxyGroup
argument_list|)
expr_stmt|;
name|String
index|[]
name|userGroups
init|=
operator|new
name|String
index|[]
block|{
name|proxyGroup
block|}
decl_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|proxyUser
argument_list|,
name|userGroups
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"u1"
argument_list|,
name|userGroups
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"u2"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"gg"
block|}
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|dataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|fileSystem
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/hadoop/mapred/system"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/hadoop/mapred/system"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rwx------"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|nnURI
init|=
name|fileSystem
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|numDirs
init|=
literal|1
decl_stmt|;
name|String
index|[]
name|racks
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|hosts
init|=
literal|null
decl_stmt|;
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|taskTrackers
argument_list|,
name|nnURI
argument_list|,
name|numDirs
argument_list|,
name|racks
argument_list|,
name|hosts
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getJobConf ()
specifier|protected
name|JobConf
name|getJobConf
parameter_list|()
block|{
return|return
name|mrCluster
operator|.
name|createJobConf
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|mrRun ()
specifier|private
name|void
name|mrRun
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getJobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|inputDir
init|=
operator|new
name|Path
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|inputDir
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inputDir
argument_list|,
literal|"data.txt"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Path
name|outputDir
init|=
operator|new
name|Path
argument_list|(
literal|"output"
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|getJobConf
argument_list|()
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"mapred.map.tasks"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"mapred.map.max.attempts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"mapred.reduce.max.attempts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
literal|"mapred.input.dir"
argument_list|,
name|inputDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
literal|"mapred.output.dir"
argument_list|,
name|outputDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|JobClient
name|jobClient
init|=
operator|new
name|JobClient
argument_list|(
name|jobConf
argument_list|)
decl_stmt|;
name|RunningJob
name|runJob
init|=
name|jobClient
operator|.
name|submitJob
argument_list|(
name|jobConf
argument_list|)
decl_stmt|;
name|runJob
operator|.
name|waitForCompletion
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|runJob
operator|.
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runJob
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|__testCurrentUser ()
specifier|public
name|void
name|__testCurrentUser
parameter_list|()
throws|throws
name|Exception
block|{
name|mrRun
argument_list|()
expr_stmt|;
block|}
DECL|method|testValidProxyUser ()
specifier|public
name|void
name|testValidProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"u1"
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|mrRun
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|___testInvalidProxyUser ()
specifier|public
name|void
name|___testInvalidProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"u2"
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|mrRun
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|ex
parameter_list|)
block|{
comment|//nop
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

