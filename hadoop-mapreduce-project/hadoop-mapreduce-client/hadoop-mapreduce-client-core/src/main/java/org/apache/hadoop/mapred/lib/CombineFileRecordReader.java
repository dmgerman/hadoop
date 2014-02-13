begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
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
name|mapred
operator|.
name|*
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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

begin_comment
comment|/**  * A generic RecordReader that can hand out different recordReaders  * for each chunk in a {@link CombineFileSplit}.  * A CombineFileSplit can combine data chunks from multiple files.   * This class allows using different RecordReaders for processing  * these data chunks from different files.  * @see CombineFileSplit  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|CombineFileRecordReader
specifier|public
class|class
name|CombineFileRecordReader
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|constructorSignature
specifier|static
specifier|final
name|Class
index|[]
name|constructorSignature
init|=
operator|new
name|Class
index|[]
block|{
name|CombineFileSplit
operator|.
name|class
block|,
name|Configuration
operator|.
name|class
block|,
name|Reporter
operator|.
name|class
block|,
name|Integer
operator|.
name|class
block|}
decl_stmt|;
DECL|field|split
specifier|protected
name|CombineFileSplit
name|split
decl_stmt|;
DECL|field|jc
specifier|protected
name|JobConf
name|jc
decl_stmt|;
DECL|field|reporter
specifier|protected
name|Reporter
name|reporter
decl_stmt|;
DECL|field|rrClass
specifier|protected
name|Class
argument_list|<
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|rrClass
decl_stmt|;
DECL|field|rrConstructor
specifier|protected
name|Constructor
argument_list|<
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|rrConstructor
decl_stmt|;
DECL|field|fs
specifier|protected
name|FileSystem
name|fs
decl_stmt|;
DECL|field|idx
specifier|protected
name|int
name|idx
decl_stmt|;
DECL|field|progress
specifier|protected
name|long
name|progress
decl_stmt|;
DECL|field|curReader
specifier|protected
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|curReader
decl_stmt|;
DECL|method|next (K key, V value)
specifier|public
name|boolean
name|next
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
while|while
condition|(
operator|(
name|curReader
operator|==
literal|null
operator|)
operator|||
operator|!
name|curReader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|initNextRecordReader
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|createKey ()
specifier|public
name|K
name|createKey
parameter_list|()
block|{
return|return
name|curReader
operator|.
name|createKey
argument_list|()
return|;
block|}
DECL|method|createValue ()
specifier|public
name|V
name|createValue
parameter_list|()
block|{
return|return
name|curReader
operator|.
name|createValue
argument_list|()
return|;
block|}
comment|/**    * return the amount of data processed    */
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|progress
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|curReader
operator|!=
literal|null
condition|)
block|{
name|curReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|curReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * return progress based on the amount of data processed so far.    */
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
literal|1.0f
argument_list|,
name|progress
operator|/
call|(
name|float
call|)
argument_list|(
name|split
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * A generic RecordReader that can hand out different recordReaders    * for each chunk in the CombineFileSplit.    */
DECL|method|CombineFileRecordReader (JobConf job, CombineFileSplit split, Reporter reporter, Class<RecordReader<K, V>> rrClass)
specifier|public
name|CombineFileRecordReader
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|CombineFileSplit
name|split
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|Class
argument_list|<
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|rrClass
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|split
operator|=
name|split
expr_stmt|;
name|this
operator|.
name|jc
operator|=
name|job
expr_stmt|;
name|this
operator|.
name|rrClass
operator|=
name|rrClass
expr_stmt|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
name|this
operator|.
name|idx
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|curReader
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|progress
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|rrConstructor
operator|=
name|rrClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|constructorSignature
argument_list|)
expr_stmt|;
name|rrConstructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|rrClass
operator|.
name|getName
argument_list|()
operator|+
literal|" does not have valid constructor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|initNextRecordReader
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the record reader for the next chunk in this CombineFileSplit.    */
DECL|method|initNextRecordReader ()
specifier|protected
name|boolean
name|initNextRecordReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|curReader
operator|!=
literal|null
condition|)
block|{
name|curReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|curReader
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|progress
operator|+=
name|split
operator|.
name|getLength
argument_list|(
name|idx
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// done processing so far
block|}
block|}
comment|// if all chunks have been processed, nothing more to do.
if|if
condition|(
name|idx
operator|==
name|split
operator|.
name|getNumPaths
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|reporter
operator|.
name|progress
argument_list|()
expr_stmt|;
comment|// get a record reader for the idx-th chunk
try|try
block|{
name|curReader
operator|=
name|rrConstructor
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|split
block|,
name|jc
block|,
name|reporter
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|idx
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// setup some helper config variables.
name|jc
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MAP_INPUT_FILE
argument_list|,
name|split
operator|.
name|getPath
argument_list|(
name|idx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|jc
operator|.
name|setLong
argument_list|(
name|JobContext
operator|.
name|MAP_INPUT_START
argument_list|,
name|split
operator|.
name|getOffset
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|jc
operator|.
name|setLong
argument_list|(
name|JobContext
operator|.
name|MAP_INPUT_PATH
argument_list|,
name|split
operator|.
name|getLength
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|idx
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

