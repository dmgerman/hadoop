begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

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

begin_comment
comment|/**  * Used by {@link AliyunOSSFileSystem} as an task that submitted  * to the thread pool to accelerate the copy progress.  * Each AliyunOSSCopyFileTask copies one file from src path to dst path  */
end_comment

begin_class
DECL|class|AliyunOSSCopyFileTask
specifier|public
class|class
name|AliyunOSSCopyFileTask
implements|implements
name|Runnable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AliyunOSSCopyFileTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|store
specifier|private
name|AliyunOSSFileSystemStore
name|store
decl_stmt|;
DECL|field|srcKey
specifier|private
name|String
name|srcKey
decl_stmt|;
DECL|field|srcLen
specifier|private
name|long
name|srcLen
decl_stmt|;
DECL|field|dstKey
specifier|private
name|String
name|dstKey
decl_stmt|;
DECL|field|copyFileContext
specifier|private
name|AliyunOSSCopyFileContext
name|copyFileContext
decl_stmt|;
DECL|method|AliyunOSSCopyFileTask (AliyunOSSFileSystemStore store, String srcKey, long srcLen, String dstKey, AliyunOSSCopyFileContext copyFileContext)
specifier|public
name|AliyunOSSCopyFileTask
parameter_list|(
name|AliyunOSSFileSystemStore
name|store
parameter_list|,
name|String
name|srcKey
parameter_list|,
name|long
name|srcLen
parameter_list|,
name|String
name|dstKey
parameter_list|,
name|AliyunOSSCopyFileContext
name|copyFileContext
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|srcKey
operator|=
name|srcKey
expr_stmt|;
name|this
operator|.
name|srcLen
operator|=
name|srcLen
expr_stmt|;
name|this
operator|.
name|dstKey
operator|=
name|dstKey
expr_stmt|;
name|this
operator|.
name|copyFileContext
operator|=
name|copyFileContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fail
operator|=
operator|!
name|store
operator|.
name|copyFile
argument_list|(
name|srcKey
argument_list|,
name|srcLen
argument_list|,
name|dstKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception thrown when copy from "
operator|+
name|srcKey
operator|+
literal|" to "
operator|+
name|dstKey
operator|+
literal|", exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|copyFileContext
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|copyFileContext
operator|.
name|setCopyFailure
argument_list|(
name|fail
argument_list|)
expr_stmt|;
block|}
name|copyFileContext
operator|.
name|incCopiesFinish
argument_list|()
expr_stmt|;
name|copyFileContext
operator|.
name|signalAll
argument_list|()
expr_stmt|;
name|copyFileContext
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

