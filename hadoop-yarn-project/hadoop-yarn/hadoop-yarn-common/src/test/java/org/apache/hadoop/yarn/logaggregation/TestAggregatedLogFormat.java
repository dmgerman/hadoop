begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
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
name|DataInputStream
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
name|FileNotFoundException
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|Log
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
name|LogFactory
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
name|CommonConfigurationKeysPublic
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|util
operator|.
name|StringUtils
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
name|TestContainerId
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
name|ApplicationAttemptId
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
name|ContainerId
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogKey
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogReader
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogValue
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogWriter
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
name|Times
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
name|Assume
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

begin_class
DECL|class|TestAggregatedLogFormat
specifier|public
class|class
name|TestAggregatedLogFormat
block|{
DECL|field|testWorkDir
specifier|private
specifier|static
specifier|final
name|File
name|testWorkDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"TestAggregatedLogFormat"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|filler
specifier|private
specifier|static
specifier|final
name|char
name|filler
init|=
literal|'x'
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestAggregatedLogFormat
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Before
annotation|@
name|After
DECL|method|cleanupTestDir ()
specifier|public
name|void
name|cleanupTestDir
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|workDirPath
init|=
operator|new
name|Path
argument_list|(
name|testWorkDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning test directory ["
operator|+
name|workDirPath
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|workDirPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//Test for Corrupted AggregatedLogs. The Logs should not write more data
comment|//if Logvalue.write() is called and the application is still
comment|//appending to logs
annotation|@
name|Test
DECL|method|testForCorruptedAggregatedLogs ()
specifier|public
name|void
name|testForCorruptedAggregatedLogs
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|testWorkDir
argument_list|,
literal|"testReadAcontainerLogs1"
argument_list|)
decl_stmt|;
name|Path
name|remoteAppLogFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"aggregatedLogFile"
argument_list|)
decl_stmt|;
name|Path
name|srcFileRoot
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"srcFiles"
argument_list|)
decl_stmt|;
name|ContainerId
name|testContainerId
init|=
name|TestContainerId
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Path
name|t
init|=
operator|new
name|Path
argument_list|(
name|srcFileRoot
argument_list|,
name|testContainerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|srcFilePath
init|=
operator|new
name|Path
argument_list|(
name|t
argument_list|,
name|testContainerId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|numChars
init|=
literal|950000
decl_stmt|;
name|writeSrcFileAndALog
argument_list|(
name|srcFilePath
argument_list|,
literal|"stdout"
argument_list|,
name|numChars
argument_list|,
name|remoteAppLogFile
argument_list|,
name|srcFileRoot
argument_list|,
name|testContainerId
argument_list|)
expr_stmt|;
name|LogReader
name|logReader
init|=
operator|new
name|LogReader
argument_list|(
name|conf
argument_list|,
name|remoteAppLogFile
argument_list|)
decl_stmt|;
name|LogKey
name|rLogKey
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
name|DataInputStream
name|dis
init|=
name|logReader
operator|.
name|next
argument_list|(
name|rLogKey
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|LogReader
operator|.
name|readAcontainerLogs
argument_list|(
name|dis
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NumberFormatException"
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Aggregated logs are corrupted."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeSrcFileAndALog (Path srcFilePath, String fileName, final long length, Path remoteAppLogFile, Path srcFileRoot, ContainerId testContainerId)
specifier|private
name|void
name|writeSrcFileAndALog
parameter_list|(
name|Path
name|srcFilePath
parameter_list|,
name|String
name|fileName
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
name|Path
name|remoteAppLogFile
parameter_list|,
name|Path
name|srcFileRoot
parameter_list|,
name|ContainerId
name|testContainerId
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|srcFilePath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create directory : "
operator|+
name|dir
argument_list|)
throw|;
block|}
block|}
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|srcFilePath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
specifier|final
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF8"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ch
init|=
name|filler
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
try|try
init|(
name|LogWriter
name|logWriter
init|=
operator|new
name|LogWriter
argument_list|()
init|)
block|{
name|logWriter
operator|.
name|initialize
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|remoteAppLogFile
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|LogKey
name|logKey
init|=
operator|new
name|LogKey
argument_list|(
name|testContainerId
argument_list|)
decl_stmt|;
name|LogValue
name|logValue
init|=
name|spy
argument_list|(
operator|new
name|LogValue
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|srcFileRoot
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|testContainerId
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
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
name|length
operator|/
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|osw
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
literal|2
operator|*
name|length
operator|)
operator|/
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|osw
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|osw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//Wait till the osw is partially written
comment|//aggregation starts once the ows has completed 1/3rd of its work
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
comment|//Aggregate The Logs
name|logWriter
operator|.
name|append
argument_list|(
name|logKey
argument_list|,
name|logValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadAcontainerLogs1 ()
specifier|public
name|void
name|testReadAcontainerLogs1
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Verify the output generated by readAContainerLogs(DataInputStream, Writer, logUploadedTime)
name|testReadAcontainerLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//Verify the output generated by readAContainerLogs(DataInputStream, Writer)
name|testReadAcontainerLog
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadAcontainerLog (boolean logUploadedTime)
specifier|private
name|void
name|testReadAcontainerLog
parameter_list|(
name|boolean
name|logUploadedTime
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|testWorkDir
argument_list|,
literal|"testReadAcontainerLogs1"
argument_list|)
decl_stmt|;
name|Path
name|remoteAppLogFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"aggregatedLogFile"
argument_list|)
decl_stmt|;
name|Path
name|srcFileRoot
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"srcFiles"
argument_list|)
decl_stmt|;
name|ContainerId
name|testContainerId
init|=
name|TestContainerId
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Path
name|t
init|=
operator|new
name|Path
argument_list|(
name|srcFileRoot
argument_list|,
name|testContainerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|srcFilePath
init|=
operator|new
name|Path
argument_list|(
name|t
argument_list|,
name|testContainerId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numChars
init|=
literal|80000
decl_stmt|;
comment|// create a sub-folder under srcFilePath
comment|// and create file logs in this sub-folder.
comment|// We only aggregate top level files.
comment|// So, this log file should be ignored.
name|Path
name|subDir
init|=
operator|new
name|Path
argument_list|(
name|srcFilePath
argument_list|,
literal|"subDir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|subDir
argument_list|)
expr_stmt|;
name|writeSrcFile
argument_list|(
name|subDir
argument_list|,
literal|"logs"
argument_list|,
name|numChars
argument_list|)
expr_stmt|;
comment|// create file stderr and stdout in containerLogDir
name|writeSrcFile
argument_list|(
name|srcFilePath
argument_list|,
literal|"stderr"
argument_list|,
name|numChars
argument_list|)
expr_stmt|;
name|writeSrcFile
argument_list|(
name|srcFilePath
argument_list|,
literal|"stdout"
argument_list|,
name|numChars
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
try|try
init|(
name|LogWriter
name|logWriter
init|=
operator|new
name|LogWriter
argument_list|()
init|)
block|{
name|logWriter
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|remoteAppLogFile
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|LogKey
name|logKey
init|=
operator|new
name|LogKey
argument_list|(
name|testContainerId
argument_list|)
decl_stmt|;
name|LogValue
name|logValue
init|=
operator|new
name|LogValue
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|srcFileRoot
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|testContainerId
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
comment|// When we try to open FileInputStream for stderr, it will throw out an
comment|// IOException. Skip the log aggregation for stderr.
name|LogValue
name|spyLogValue
init|=
name|spy
argument_list|(
name|logValue
argument_list|)
decl_stmt|;
name|File
name|errorFile
init|=
operator|new
name|File
argument_list|(
operator|(
operator|new
name|Path
argument_list|(
name|srcFilePath
argument_list|,
literal|"stderr"
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Mock can not open FileInputStream"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyLogValue
argument_list|)
operator|.
name|secureOpenFile
argument_list|(
name|errorFile
argument_list|)
expr_stmt|;
name|logWriter
operator|.
name|append
argument_list|(
name|logKey
argument_list|,
name|spyLogValue
argument_list|)
expr_stmt|;
block|}
comment|// make sure permission are correct on the file
name|FileStatus
name|fsStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|remoteAppLogFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"permissions on log aggregation file are wrong"
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0640
argument_list|)
argument_list|,
name|fsStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|LogReader
name|logReader
init|=
operator|new
name|LogReader
argument_list|(
name|conf
argument_list|,
name|remoteAppLogFile
argument_list|)
decl_stmt|;
name|LogKey
name|rLogKey
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
name|DataInputStream
name|dis
init|=
name|logReader
operator|.
name|next
argument_list|(
name|rLogKey
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|logUploadedTime
condition|)
block|{
name|LogReader
operator|.
name|readAcontainerLogs
argument_list|(
name|dis
argument_list|,
name|writer
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LogReader
operator|.
name|readAcontainerLogs
argument_list|(
name|dis
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
comment|// We should only do the log aggregation for stdout.
comment|// Since we could not open the fileInputStream for stderr, this file is not
comment|// aggregated.
name|String
name|s
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|expectedLength
init|=
literal|"LogType:stdout"
operator|.
name|length
argument_list|()
operator|+
operator|(
name|logUploadedTime
condition|?
operator|(
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"Log Upload Time:"
operator|+
name|Times
operator|.
name|format
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|)
operator|.
name|length
argument_list|()
else|:
literal|0
operator|)
operator|+
operator|(
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"LogLength:"
operator|+
name|numChars
operator|)
operator|.
name|length
argument_list|()
operator|+
operator|(
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"Log Contents:"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|)
operator|.
name|length
argument_list|()
operator|+
name|numChars
operator|+
operator|(
literal|"\n"
operator|)
operator|.
name|length
argument_list|()
operator|+
operator|(
literal|"End of LogType:stdout"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"LogType not matched"
argument_list|,
name|s
operator|.
name|contains
argument_list|(
literal|"LogType:stdout"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"log file:stderr should not be aggregated."
argument_list|,
operator|!
name|s
operator|.
name|contains
argument_list|(
literal|"LogType:stderr"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"log file:logs should not be aggregated."
argument_list|,
operator|!
name|s
operator|.
name|contains
argument_list|(
literal|"LogType:logs"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"LogLength not matched"
argument_list|,
name|s
operator|.
name|contains
argument_list|(
literal|"LogLength:"
operator|+
name|numChars
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Log Contents not matched"
argument_list|,
name|s
operator|.
name|contains
argument_list|(
literal|"Log Contents"
argument_list|)
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numChars
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filler
argument_list|)
expr_stmt|;
block|}
name|String
name|expectedContent
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Log content incorrect"
argument_list|,
name|s
operator|.
name|contains
argument_list|(
name|expectedContent
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedLength
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testContainerLogsFileAccess ()
specifier|public
name|void
name|testContainerLogsFileAccess
parameter_list|()
throws|throws
name|IOException
block|{
comment|// This test will run only if NativeIO is enabled as SecureIOUtils
comment|// require it to be enabled.
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|testWorkDir
argument_list|,
literal|"testContainerLogsFileAccess1"
argument_list|)
decl_stmt|;
name|Path
name|remoteAppLogFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"aggregatedLogFile"
argument_list|)
decl_stmt|;
name|Path
name|srcFileRoot
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"srcFiles"
argument_list|)
decl_stmt|;
name|String
name|data
init|=
literal|"Log File content for container : "
decl_stmt|;
comment|// Creating files for container1. Log aggregator will try to read log files
comment|// with illegal user.
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|testContainerId1
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
operator|new
name|Path
argument_list|(
name|srcFileRoot
argument_list|,
name|testContainerId1
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|srcFilePath1
init|=
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|testContainerId1
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|stdout
init|=
literal|"stdout"
decl_stmt|;
name|String
name|stderr
init|=
literal|"stderr"
decl_stmt|;
name|writeSrcFile
argument_list|(
name|srcFilePath1
argument_list|,
name|stdout
argument_list|,
name|data
operator|+
name|testContainerId1
operator|.
name|toString
argument_list|()
operator|+
name|stdout
argument_list|)
expr_stmt|;
name|writeSrcFile
argument_list|(
name|srcFilePath1
argument_list|,
name|stderr
argument_list|,
name|data
operator|+
name|testContainerId1
operator|.
name|toString
argument_list|()
operator|+
name|stderr
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
try|try
init|(
name|LogWriter
name|logWriter
init|=
operator|new
name|LogWriter
argument_list|()
init|)
block|{
name|logWriter
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|remoteAppLogFile
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|LogKey
name|logKey
init|=
operator|new
name|LogKey
argument_list|(
name|testContainerId1
argument_list|)
decl_stmt|;
name|String
name|randomUser
init|=
literal|"randomUser"
decl_stmt|;
name|LogValue
name|logValue
init|=
name|spy
argument_list|(
operator|new
name|LogValue
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|srcFileRoot
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|testContainerId1
argument_list|,
name|randomUser
argument_list|)
argument_list|)
decl_stmt|;
comment|// It is trying simulate a situation where first log file is owned by
comment|// different user (probably symlink) and second one by the user itself.
comment|// The first file should not be aggregated. Because this log file has
comment|// the invalid user name.
name|when
argument_list|(
name|logValue
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|randomUser
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|logWriter
operator|.
name|append
argument_list|(
name|logKey
argument_list|,
name|logValue
argument_list|)
expr_stmt|;
block|}
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
operator|new
name|File
argument_list|(
name|remoteAppLogFile
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
name|String
name|expectedOwner
init|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
specifier|final
name|String
name|adminsGroupString
init|=
literal|"Administrators"
decl_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|adminsGroupString
argument_list|)
condition|)
block|{
name|expectedOwner
operator|=
name|adminsGroupString
expr_stmt|;
block|}
block|}
comment|// This file: stderr should not be aggregated.
comment|// And we will not aggregate the log message.
name|String
name|stdoutFile1
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|File
operator|.
name|separator
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"srcFiles"
block|,
name|testContainerId1
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
block|,
name|testContainerId1
operator|.
name|toString
argument_list|()
block|,
name|stderr
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|// The file: stdout is expected to be aggregated.
name|String
name|stdoutFile2
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|File
operator|.
name|separator
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"srcFiles"
block|,
name|testContainerId1
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
block|,
name|testContainerId1
operator|.
name|toString
argument_list|()
block|,
name|stdout
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|message2
init|=
literal|"Owner '"
operator|+
name|expectedOwner
operator|+
literal|"' for path "
operator|+
name|stdoutFile2
operator|+
literal|" did not match expected owner '"
operator|+
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"'"
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|line
operator|.
name|contains
argument_list|(
name|message2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|line
operator|.
name|contains
argument_list|(
name|data
operator|+
name|testContainerId1
operator|.
name|toString
argument_list|()
operator|+
name|stderr
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|line
operator|.
name|contains
argument_list|(
name|data
operator|+
name|testContainerId1
operator|.
name|toString
argument_list|()
operator|+
name|stdout
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeSrcFile (Path srcFilePath, String fileName, long length)
specifier|private
name|void
name|writeSrcFile
parameter_list|(
name|Path
name|srcFilePath
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStreamWriter
name|osw
init|=
name|getOutputStreamWriter
argument_list|(
name|srcFilePath
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|int
name|ch
init|=
name|filler
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|osw
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|osw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|writeSrcFile (Path srcFilePath, String fileName, String data)
specifier|private
name|void
name|writeSrcFile
parameter_list|(
name|Path
name|srcFilePath
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStreamWriter
name|osw
init|=
name|getOutputStreamWriter
argument_list|(
name|srcFilePath
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|osw
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|osw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getOutputStreamWriter (Path srcFilePath, String fileName)
specifier|private
name|OutputStreamWriter
name|getOutputStreamWriter
parameter_list|(
name|Path
name|srcFilePath
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedEncodingException
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|srcFilePath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create directory : "
operator|+
name|dir
argument_list|)
throw|;
block|}
block|}
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|srcFilePath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF8"
argument_list|)
decl_stmt|;
return|return
name|osw
return|;
block|}
block|}
end_class

end_unit

