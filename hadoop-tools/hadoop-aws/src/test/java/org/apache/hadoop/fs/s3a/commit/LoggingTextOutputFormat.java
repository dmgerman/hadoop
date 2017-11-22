begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|compress
operator|.
name|CompressionCodec
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
name|compress
operator|.
name|GzipCodec
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
name|MRJobConfig
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
name|OutputFormat
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
name|TextOutputFormat
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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * A subclass of {@link TextOutputFormat} which logs what is happening, and  * returns a {@link LoggingLineRecordWriter} which allows the caller  * to get the destination path.  * @param<K> key  * @param<V> value  */
end_comment

begin_class
DECL|class|LoggingTextOutputFormat
specifier|public
class|class
name|LoggingTextOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LoggingTextOutputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.fs.s3a.commit.LoggingTextOutputFormat"
decl_stmt|;
annotation|@
name|Override
DECL|method|getRecordWriter (TaskAttemptContext job)
specifier|public
name|LoggingLineRecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|job
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|isCompressed
init|=
name|getCompressOutput
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|String
name|keyValueSeparator
init|=
name|conf
operator|.
name|get
argument_list|(
name|SEPARATOR
argument_list|,
literal|"\t"
argument_list|)
decl_stmt|;
name|CompressionCodec
name|codec
init|=
literal|null
decl_stmt|;
name|String
name|extension
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|isCompressed
condition|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|codecClass
init|=
name|getOutputCompressorClass
argument_list|(
name|job
argument_list|,
name|GzipCodec
operator|.
name|class
argument_list|)
decl_stmt|;
name|codec
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|codecClass
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|extension
operator|=
name|codec
operator|.
name|getDefaultExtension
argument_list|()
expr_stmt|;
block|}
name|Path
name|file
init|=
name|getDefaultWorkFile
argument_list|(
name|job
argument_list|,
name|extension
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|file
operator|.
name|getFileSystem
argument_list|(
name|conf
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating LineRecordWriter with destination {}"
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressed
condition|)
block|{
return|return
operator|new
name|LoggingLineRecordWriter
argument_list|<>
argument_list|(
name|file
argument_list|,
operator|new
name|DataOutputStream
argument_list|(
name|codec
operator|.
name|createOutputStream
argument_list|(
name|fileOut
argument_list|)
argument_list|)
argument_list|,
name|keyValueSeparator
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|LoggingLineRecordWriter
argument_list|<>
argument_list|(
name|file
argument_list|,
name|fileOut
argument_list|,
name|keyValueSeparator
argument_list|)
return|;
block|}
block|}
comment|/**    * Write a line; counts the number of lines written and logs @ debug in the    * {@code close()} call.    * @param<K> key    * @param<V> value    */
DECL|class|LoggingLineRecordWriter
specifier|public
specifier|static
class|class
name|LoggingLineRecordWriter
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|LineRecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|dest
specifier|private
specifier|final
name|Path
name|dest
decl_stmt|;
DECL|field|lines
specifier|private
name|long
name|lines
decl_stmt|;
DECL|method|LoggingLineRecordWriter (Path dest, DataOutputStream out, String keyValueSeparator)
specifier|public
name|LoggingLineRecordWriter
parameter_list|(
name|Path
name|dest
parameter_list|,
name|DataOutputStream
name|out
parameter_list|,
name|String
name|keyValueSeparator
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
name|keyValueSeparator
argument_list|)
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
block|}
DECL|method|LoggingLineRecordWriter (DataOutputStream out, Path dest)
specifier|public
name|LoggingLineRecordWriter
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|Path
name|dest
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (K key, V value)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|lines
operator|++
expr_stmt|;
block|}
DECL|method|close (TaskAttemptContext context)
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing output file {} with {} lines :{}"
argument_list|,
name|dest
argument_list|,
name|lines
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDest ()
specifier|public
name|Path
name|getDest
parameter_list|()
block|{
return|return
name|dest
return|;
block|}
DECL|method|getLines ()
specifier|public
name|long
name|getLines
parameter_list|()
block|{
return|return
name|lines
return|;
block|}
block|}
comment|/**    * Bind to a configuration for job submission.    * @param conf configuration    */
DECL|method|bind (Configuration conf)
specifier|public
specifier|static
name|void
name|bind
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|MRJobConfig
operator|.
name|OUTPUT_FORMAT_CLASS_ATTR
argument_list|,
name|LoggingTextOutputFormat
operator|.
name|class
argument_list|,
name|OutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

