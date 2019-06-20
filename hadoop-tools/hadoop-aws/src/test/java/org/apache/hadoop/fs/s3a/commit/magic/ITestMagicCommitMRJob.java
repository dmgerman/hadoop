begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.magic
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
operator|.
name|commit
operator|.
name|magic
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|s3a
operator|.
name|S3AFileSystem
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
name|AbstractITCommitMRJob
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
name|files
operator|.
name|SuccessData
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
import|import static
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
name|S3ATestUtils
operator|.
name|lsR
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
name|s3a
operator|.
name|S3AUtils
operator|.
name|applyLocatedFiles
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
name|s3a
operator|.
name|commit
operator|.
name|CommitConstants
operator|.
name|*
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_comment
comment|/**  * Full integration test for the Magic Committer.  *  * There's no need to disable the committer setting for the filesystem here,  * because the committers are being instantiated in their own processes;  * the settings in {@link AbstractITCommitMRJob#applyCustomConfigOptions(JobConf)} are  * passed down to these processes.  */
end_comment

begin_class
DECL|class|ITestMagicCommitMRJob
specifier|public
specifier|final
class|class
name|ITestMagicCommitMRJob
extends|extends
name|AbstractITCommitMRJob
block|{
comment|/**    * The static cluster binding with the lifecycle of this test; served    * through instance-level methods for sharing across methods in the    * suite.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticNonFinalField"
argument_list|)
DECL|field|clusterBinding
specifier|private
specifier|static
name|ClusterBinding
name|clusterBinding
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClusters ()
specifier|public
specifier|static
name|void
name|setupClusters
parameter_list|()
throws|throws
name|IOException
block|{
name|clusterBinding
operator|=
name|createCluster
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClusters ()
specifier|public
specifier|static
name|void
name|teardownClusters
parameter_list|()
throws|throws
name|IOException
block|{
name|terminateCluster
argument_list|(
name|clusterBinding
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClusterBinding ()
specifier|public
name|ClusterBinding
name|getClusterBinding
parameter_list|()
block|{
return|return
name|clusterBinding
return|;
block|}
comment|/**    * Need consistency here.    * @return false    */
annotation|@
name|Override
DECL|method|useInconsistentClient ()
specifier|public
name|boolean
name|useInconsistentClient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|committerName ()
specifier|protected
name|String
name|committerName
parameter_list|()
block|{
return|return
name|MagicS3GuardCommitter
operator|.
name|NAME
return|;
block|}
comment|/**    * Turn on the magic commit support for the FS, else nothing will work.    * @param conf configuration    */
annotation|@
name|Override
DECL|method|applyCustomConfigOptions (JobConf conf)
specifier|protected
name|void
name|applyCustomConfigOptions
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|MAGIC_COMMITTER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the magic dir was cleaned up.    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|customPostExecutionValidation (Path destPath, SuccessData successData)
specifier|protected
name|void
name|customPostExecutionValidation
parameter_list|(
name|Path
name|destPath
parameter_list|,
name|SuccessData
name|successData
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|magicDir
init|=
operator|new
name|Path
argument_list|(
name|destPath
argument_list|,
name|MAGIC
argument_list|)
decl_stmt|;
comment|// if an FNFE isn't raised on getFileStatus, list out the directory
comment|// tree
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// log the contents
name|lsR
argument_list|(
name|fs
argument_list|,
name|destPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
specifier|final
name|FileStatus
name|st
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|magicDir
argument_list|)
decl_stmt|;
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Found magic dir which should"
operator|+
literal|" have been deleted at "
argument_list|)
operator|.
name|append
argument_list|(
name|st
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|applyLocatedFiles
argument_list|(
name|fs
operator|.
name|listFiles
argument_list|(
name|magicDir
argument_list|,
literal|true
argument_list|)
argument_list|,
parameter_list|(
name|status
parameter_list|)
lambda|->
name|result
operator|.
name|append
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

