begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertNull
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileReader
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|fs
operator|.
name|FileStatus
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
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|HdfsConfiguration
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
name|HftpFileSystem
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsTestUtil
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
name|web
operator|.
name|WebHdfsFileSystem
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
name|AccessControlException
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
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|PatternLayout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|RollingFileAppender
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

begin_comment
comment|/**  * A JUnit test that audit logs are generated  */
end_comment

begin_class
DECL|class|TestAuditLogs
specifier|public
class|class
name|TestAuditLogs
block|{
DECL|field|auditLogFile
specifier|static
specifier|final
name|String
name|auditLogFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.dir"
argument_list|,
literal|"build/test"
argument_list|)
operator|+
literal|"/audit.log"
decl_stmt|;
comment|// Pattern for:
comment|// allowed=(true|false) ugi=name ip=/address cmd={cmd} src={path} dst=null perm=null
DECL|field|auditPattern
specifier|static
specifier|final
name|Pattern
name|auditPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"allowed=.*?\\s"
operator|+
literal|"ugi=.*?\\s"
operator|+
literal|"ip=/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s"
operator|+
literal|"cmd=.*?\\ssrc=.*?\\sdst=null\\s"
operator|+
literal|"perm=.*?"
argument_list|)
decl_stmt|;
DECL|field|successPattern
specifier|static
specifier|final
name|Pattern
name|successPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*allowed=true.*"
argument_list|)
decl_stmt|;
DECL|field|username
specifier|static
specifier|final
name|String
name|username
init|=
literal|"bob"
decl_stmt|;
DECL|field|groups
specifier|static
specifier|final
name|String
index|[]
name|groups
init|=
block|{
literal|"group1"
block|}
decl_stmt|;
DECL|field|fileName
specifier|static
specifier|final
name|String
name|fileName
init|=
literal|"/srcdat"
decl_stmt|;
DECL|field|util
name|DFSTestUtil
name|util
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
decl_stmt|;
DECL|field|fnames
name|String
name|fnames
index|[]
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|userGroupInfo
name|UserGroupInformation
name|userGroupInfo
decl_stmt|;
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
specifier|final
name|long
name|precision
init|=
literal|1L
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACCESSTIME_PRECISION_KEY
argument_list|,
name|precision
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|10000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|util
operator|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestAuditAllowed"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|20
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|util
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|fnames
operator|=
name|util
operator|.
name|getFileNames
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|util
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|userGroupInfo
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|username
argument_list|,
name|groups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardownCluster ()
specifier|public
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|.
name|cleanup
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/** test that allowed operation puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditAllowed ()
specifier|public
name|void
name|testAuditAllowed
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FileSystem
name|userfs
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
name|InputStream
name|istream
init|=
name|userfs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|val
init|=
name|istream
operator|.
name|read
argument_list|()
decl_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifyAuditLogs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to read from file"
argument_list|,
name|val
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** test that allowed stat puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditAllowedStat ()
specifier|public
name|void
name|testAuditAllowedStat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FileSystem
name|userfs
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
name|FileStatus
name|st
init|=
name|userfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|verifyAuditLogs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to stat file"
argument_list|,
name|st
operator|!=
literal|null
operator|&&
name|st
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test that denied operation puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditDenied ()
specifier|public
name|void
name|testAuditDenied
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FileSystem
name|userfs
init|=
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0600
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|file
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
try|try
block|{
name|userfs
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"open must not succeed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"got access denied, as expected."
argument_list|)
expr_stmt|;
block|}
name|verifyAuditLogs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** test that access via webhdfs puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditWebHdfs ()
specifier|public
name|void
name|testAuditWebHdfs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0644
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|file
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
name|WebHdfsFileSystem
name|webfs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|InputStream
name|istream
init|=
name|webfs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|val
init|=
name|istream
operator|.
name|read
argument_list|()
decl_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifyAuditLogsRepeat
argument_list|(
literal|true
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to read from file"
argument_list|,
name|val
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** test that stat via webhdfs puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditWebHdfsStat ()
specifier|public
name|void
name|testAuditWebHdfsStat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0644
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|file
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
name|WebHdfsFileSystem
name|webfs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
name|st
init|=
name|webfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|verifyAuditLogs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to stat file"
argument_list|,
name|st
operator|!=
literal|null
operator|&&
name|st
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test that access via Hftp puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditHftp ()
specifier|public
name|void
name|testAuditHftp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|hftpUri
init|=
literal|"hftp://"
operator|+
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|HftpFileSystem
name|hftpFs
init|=
literal|null
decl_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
try|try
block|{
name|hftpFs
operator|=
operator|(
name|HftpFileSystem
operator|)
operator|new
name|Path
argument_list|(
name|hftpUri
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|InputStream
name|istream
init|=
name|hftpFs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|val
init|=
name|istream
operator|.
name|read
argument_list|()
decl_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifyAuditLogs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|hftpFs
operator|!=
literal|null
condition|)
name|hftpFs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** test that denied access via webhdfs puts proper entry in audit log */
annotation|@
name|Test
DECL|method|testAuditWebHdfsDenied ()
specifier|public
name|void
name|testAuditWebHdfsDenied
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|fnames
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0600
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|file
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setupAuditLogs
argument_list|()
expr_stmt|;
try|try
block|{
name|WebHdfsFileSystem
name|webfs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|userGroupInfo
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|InputStream
name|istream
init|=
name|webfs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|val
init|=
name|istream
operator|.
name|read
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"open+read must not succeed, got "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|E
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"got access denied, as expected."
argument_list|)
expr_stmt|;
block|}
name|verifyAuditLogsRepeat
argument_list|(
literal|false
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/** Sets up log4j logger for auditlogs */
DECL|method|setupAuditLogs ()
specifier|private
name|void
name|setupAuditLogs
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|auditLogFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|FSNamesystem
operator|.
name|auditLog
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|PatternLayout
name|layout
init|=
operator|new
name|PatternLayout
argument_list|(
literal|"%m%n"
argument_list|)
decl_stmt|;
name|RollingFileAppender
name|appender
init|=
operator|new
name|RollingFileAppender
argument_list|(
name|layout
argument_list|,
name|auditLogFile
argument_list|)
decl_stmt|;
name|logger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
comment|// Ensure audit log has only one entry
DECL|method|verifyAuditLogs (boolean expectSuccess)
specifier|private
name|void
name|verifyAuditLogs
parameter_list|(
name|boolean
name|expectSuccess
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyAuditLogsRepeat
argument_list|(
name|expectSuccess
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Ensure audit log has exactly N entries
DECL|method|verifyAuditLogsRepeat (boolean expectSuccess, int ndupe)
specifier|private
name|void
name|verifyAuditLogsRepeat
parameter_list|(
name|boolean
name|expectSuccess
parameter_list|,
name|int
name|ndupe
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Turn off the logs
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|FSNamesystem
operator|.
name|auditLog
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|OFF
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|auditLogFile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|boolean
name|ret
init|=
literal|true
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ndupe
condition|;
name|i
operator|++
control|)
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected audit event not found in audit log"
argument_list|,
name|auditPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|&=
name|successPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|matches
argument_list|()
expr_stmt|;
block|}
name|assertNull
argument_list|(
literal|"Unexpected event in audit log"
argument_list|,
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected success="
operator|+
name|expectSuccess
argument_list|,
name|ret
operator|==
name|expectSuccess
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

