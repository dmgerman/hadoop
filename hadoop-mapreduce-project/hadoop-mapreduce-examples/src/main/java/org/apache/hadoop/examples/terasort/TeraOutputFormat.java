begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
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
name|io
operator|.
name|Text
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
name|InvalidJobConfException
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
name|JobContext
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
name|OutputCommitter
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
name|RecordWriter
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
name|lib
operator|.
name|output
operator|.
name|FileOutputCommitter
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
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
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
name|security
operator|.
name|TokenCache
import|;
end_import

begin_comment
comment|/**  * An output format that writes the key and value appended together.  */
end_comment

begin_class
DECL|class|TeraOutputFormat
specifier|public
class|class
name|TeraOutputFormat
extends|extends
name|FileOutputFormat
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|FINAL_SYNC_ATTRIBUTE
specifier|static
specifier|final
name|String
name|FINAL_SYNC_ATTRIBUTE
init|=
literal|"mapreduce.terasort.final.sync"
decl_stmt|;
DECL|field|committer
specifier|private
name|OutputCommitter
name|committer
init|=
literal|null
decl_stmt|;
comment|/**    * Set the requirement for a final sync before the stream is closed.    */
DECL|method|setFinalSync (JobContext job, boolean newValue)
specifier|static
name|void
name|setFinalSync
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|boolean
name|newValue
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|FINAL_SYNC_ATTRIBUTE
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Does the user want a final sync at close?    */
DECL|method|getFinalSync (JobContext job)
specifier|public
specifier|static
name|boolean
name|getFinalSync
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|FINAL_SYNC_ATTRIBUTE
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|class|TeraRecordWriter
specifier|static
class|class
name|TeraRecordWriter
extends|extends
name|RecordWriter
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|finalSync
specifier|private
name|boolean
name|finalSync
init|=
literal|false
decl_stmt|;
DECL|field|out
specifier|private
name|FSDataOutputStream
name|out
decl_stmt|;
DECL|method|TeraRecordWriter (FSDataOutputStream out, JobContext job)
specifier|public
name|TeraRecordWriter
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|,
name|JobContext
name|job
parameter_list|)
block|{
name|finalSync
operator|=
name|getFinalSync
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
DECL|method|write (Text key, Text value)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|close (TaskAttemptContext context)
specifier|public
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|finalSync
condition|)
block|{
name|out
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkOutputSpecs (JobContext job )
specifier|public
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|job
parameter_list|)
throws|throws
name|InvalidJobConfException
throws|,
name|IOException
block|{
comment|// Ensure that the output directory is set
name|Path
name|outDir
init|=
name|getOutputPath
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|outDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
literal|"Output directory not set in JobConf."
argument_list|)
throw|;
block|}
comment|// get delegation token for outDir's file system
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|job
operator|.
name|getCredentials
argument_list|()
argument_list|,
operator|new
name|Path
index|[]
block|{
name|outDir
block|}
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getRecordWriter (TaskAttemptContext job )
specifier|public
name|RecordWriter
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
name|getDefaultWorkFile
argument_list|(
name|job
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|file
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fileOut
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
return|return
operator|new
name|TeraRecordWriter
argument_list|(
name|fileOut
argument_list|,
name|job
argument_list|)
return|;
block|}
DECL|method|getOutputCommitter (TaskAttemptContext context)
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|committer
operator|==
literal|null
condition|)
block|{
name|Path
name|output
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|committer
operator|=
operator|new
name|FileOutputCommitter
argument_list|(
name|output
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|committer
return|;
block|}
block|}
end_class

end_unit

