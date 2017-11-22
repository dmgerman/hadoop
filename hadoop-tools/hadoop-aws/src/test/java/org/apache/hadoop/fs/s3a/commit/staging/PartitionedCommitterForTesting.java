begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.staging
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
name|staging
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
name|mapreduce
operator|.
name|TaskAttemptContext
import|;
end_import

begin_comment
comment|/**  * Partitioned committer overridden for better testing.  */
end_comment

begin_class
DECL|class|PartitionedCommitterForTesting
class|class
name|PartitionedCommitterForTesting
extends|extends
name|PartitionedStagingCommitter
block|{
DECL|method|PartitionedCommitterForTesting (Path outputPath, TaskAttemptContext context)
name|PartitionedCommitterForTesting
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initOutput (Path out)
specifier|protected
name|void
name|initOutput
parameter_list|(
name|Path
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|setOutputPath
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the mock FS without checking FS type.    * @param out output path    * @param config job/task config    * @return a filesystem.    * @throws IOException failure to get the FS    */
annotation|@
name|Override
DECL|method|getDestinationFS (Path out, Configuration config)
specifier|protected
name|FileSystem
name|getDestinationFS
parameter_list|(
name|Path
name|out
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|out
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
return|;
block|}
block|}
end_class

end_unit

