begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|InitiateMultipartUploadRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|LocatedFileStatus
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
name|RemoteIterator
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|staging
operator|.
name|StagingTestBase
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
name|Progressable
import|;
end_import

begin_comment
comment|/**  * Relays FS calls to the mocked FS, allows for some extra logging with  * stack traces to be included, stubbing out other methods  * where needed to avoid failures.  *  * The logging is useful for tracking  * down why there are extra calls to a method than a test would expect:  * changes in implementation details often trigger such false-positive  * test failures.  *  * This class is in the s3a package so that it has access to methods  */
end_comment

begin_class
DECL|class|MockS3AFileSystem
specifier|public
class|class
name|MockS3AFileSystem
extends|extends
name|S3AFileSystem
block|{
DECL|field|BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|BUCKET
init|=
literal|"bucket-name"
decl_stmt|;
DECL|field|FS_URI
specifier|public
specifier|static
specifier|final
name|URI
name|FS_URI
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/"
argument_list|)
decl_stmt|;
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MockS3AFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mock
specifier|private
specifier|final
name|S3AFileSystem
name|mock
decl_stmt|;
specifier|private
specifier|final
name|Pair
argument_list|<
name|StagingTestBase
operator|.
name|ClientResults
argument_list|,
DECL|field|outcome
name|StagingTestBase
operator|.
name|ClientErrors
argument_list|>
name|outcome
decl_stmt|;
comment|/** Log nothing: {@value}. */
DECL|field|LOG_NONE
specifier|public
specifier|static
specifier|final
name|int
name|LOG_NONE
init|=
literal|0
decl_stmt|;
comment|/** Log the name of the operation any arguments: {@value}.  */
DECL|field|LOG_NAME
specifier|public
specifier|static
specifier|final
name|int
name|LOG_NAME
init|=
literal|1
decl_stmt|;
comment|/** Log the entire stack of where operations are called: {@value}.  */
DECL|field|LOG_STACK
specifier|public
specifier|static
specifier|final
name|int
name|LOG_STACK
init|=
literal|2
decl_stmt|;
comment|/**    * This can be edited to set the log level of events through the    * mock FS.    */
DECL|field|logEvents
specifier|private
name|int
name|logEvents
init|=
name|LOG_NAME
decl_stmt|;
DECL|field|instrumentation
specifier|private
specifier|final
name|S3AInstrumentation
name|instrumentation
init|=
operator|new
name|S3AInstrumentation
argument_list|(
name|FS_URI
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|MockS3AFileSystem (S3AFileSystem mock, Pair<StagingTestBase.ClientResults, StagingTestBase.ClientErrors> outcome)
specifier|public
name|MockS3AFileSystem
parameter_list|(
name|S3AFileSystem
name|mock
parameter_list|,
name|Pair
argument_list|<
name|StagingTestBase
operator|.
name|ClientResults
argument_list|,
name|StagingTestBase
operator|.
name|ClientErrors
argument_list|>
name|outcome
parameter_list|)
block|{
name|this
operator|.
name|mock
operator|=
name|mock
expr_stmt|;
name|this
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
name|setUri
argument_list|(
name|FS_URI
argument_list|)
expr_stmt|;
name|setBucket
argument_list|(
name|BUCKET
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Pair
argument_list|<
name|StagingTestBase
operator|.
name|ClientResults
argument_list|,
name|StagingTestBase
operator|.
name|ClientErrors
argument_list|>
DECL|method|getOutcome ()
name|getOutcome
parameter_list|()
block|{
return|return
name|outcome
return|;
block|}
DECL|method|getLogEvents ()
specifier|public
name|int
name|getLogEvents
parameter_list|()
block|{
return|return
name|logEvents
return|;
block|}
DECL|method|setLogEvents (int logEvents)
specifier|public
name|void
name|setLogEvents
parameter_list|(
name|int
name|logEvents
parameter_list|)
block|{
name|this
operator|.
name|logEvents
operator|=
name|logEvents
expr_stmt|;
block|}
DECL|method|event (String format, Object... args)
specifier|private
name|void
name|event
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|Throwable
name|ex
init|=
literal|null
decl_stmt|;
name|String
name|s
init|=
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|logEvents
condition|)
block|{
case|case
name|LOG_STACK
case|:
name|ex
operator|=
operator|new
name|Exception
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|LOG_NAME
case|:
name|LOG
operator|.
name|info
argument_list|(
name|s
argument_list|,
name|ex
argument_list|)
expr_stmt|;
break|break;
case|case
name|LOG_NONE
case|:
default|default:
comment|//nothing
block|}
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/work"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initialize (URI name, Configuration originalConf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|name
parameter_list|,
name|Configuration
name|originalConf
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|=
name|originalConf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|isMagicCommitEnabled ()
specifier|public
name|boolean
name|isMagicCommitEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Make operation to set the s3 client public.    * @param client client.    */
annotation|@
name|Override
DECL|method|setAmazonS3Client (AmazonS3 client)
specifier|public
name|void
name|setAmazonS3Client
parameter_list|(
name|AmazonS3
name|client
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting S3 client to {}"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|super
operator|.
name|setAmazonS3Client
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exists (Path f)
specifier|public
name|boolean
name|exists
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"exists(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|exists
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishedWrite (String key, long length)
name|void
name|finishedWrite
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|length
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|open (Path f, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"open(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|open
argument_list|(
name|f
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"create(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|create
argument_list|(
name|f
argument_list|,
name|permission
argument_list|,
name|overwrite
argument_list|,
name|bufferSize
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append (Path f, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mock
operator|.
name|append
argument_list|(
name|f
argument_list|,
name|bufferSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rename (Path src, Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"rename(%s, %s)"
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"delete(%s, %s)"
argument_list|,
name|f
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|delete
argument_list|(
name|f
argument_list|,
name|recursive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"listStatus(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listFiles (Path f, boolean recursive)
specifier|public
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listFiles
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"listFiles(%s, %s)"
argument_list|,
name|f
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
return|return
operator|new
name|EmptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (Path newDir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|newDir
parameter_list|)
block|{
name|mock
operator|.
name|setWorkingDirectory
argument_list|(
name|newDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"mkdirs(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|mkdirs
argument_list|(
name|f
argument_list|,
name|permission
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|event
argument_list|(
literal|"getFileStatus(%s)"
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|mock
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultBlockSize (Path f)
specifier|public
name|long
name|getDefaultBlockSize
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
name|mock
operator|.
name|getDefaultBlockSize
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|incrementStatistic (Statistic statistic)
specifier|protected
name|void
name|incrementStatistic
parameter_list|(
name|Statistic
name|statistic
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|incrementStatistic (Statistic statistic, long count)
specifier|protected
name|void
name|incrementStatistic
parameter_list|(
name|Statistic
name|statistic
parameter_list|,
name|long
name|count
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|incrementGauge (Statistic statistic, long count)
specifier|protected
name|void
name|incrementGauge
parameter_list|(
name|Statistic
name|statistic
parameter_list|,
name|long
name|count
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|incrementReadOperations ()
specifier|public
name|void
name|incrementReadOperations
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|incrementWriteOperations ()
specifier|public
name|void
name|incrementWriteOperations
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|incrementPutStartStatistics (long bytes)
specifier|public
name|void
name|incrementPutStartStatistics
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|incrementPutCompletedStatistics (boolean success, long bytes)
specifier|public
name|void
name|incrementPutCompletedStatistics
parameter_list|(
name|boolean
name|success
parameter_list|,
name|long
name|bytes
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|incrementPutProgressStatistics (String key, long bytes)
specifier|public
name|void
name|incrementPutProgressStatistics
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|bytes
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|setOptionalMultipartUploadRequestParameters ( InitiateMultipartUploadRequest req)
specifier|protected
name|void
name|setOptionalMultipartUploadRequestParameters
parameter_list|(
name|InitiateMultipartUploadRequest
name|req
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getDefaultBlockSize ()
specifier|public
name|long
name|getDefaultBlockSize
parameter_list|()
block|{
return|return
name|mock
operator|.
name|getDefaultBlockSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|deleteObjectAtPath (Path f, String key, boolean isFile)
name|void
name|deleteObjectAtPath
parameter_list|(
name|Path
name|f
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|isFile
parameter_list|)
throws|throws
name|AmazonClientException
throws|,
name|IOException
block|{
name|deleteObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|maybeCreateFakeParentDirectory (Path path)
name|void
name|maybeCreateFakeParentDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|AmazonClientException
block|{
comment|// no-op
block|}
DECL|class|EmptyIterator
specifier|private
specifier|static
class|class
name|EmptyIterator
implements|implements
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
block|{
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|LocatedFileStatus
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"MockS3AFileSystem{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"inner mockFS="
argument_list|)
operator|.
name|append
argument_list|(
name|mock
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newCommitterStatistics ()
specifier|public
name|S3AInstrumentation
operator|.
name|CommitterStatistics
name|newCommitterStatistics
parameter_list|()
block|{
return|return
name|instrumentation
operator|.
name|newCommitterStatistics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|operationRetried (Exception ex)
specifier|public
name|void
name|operationRetried
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|/** no-op */
block|}
block|}
end_class

end_unit

