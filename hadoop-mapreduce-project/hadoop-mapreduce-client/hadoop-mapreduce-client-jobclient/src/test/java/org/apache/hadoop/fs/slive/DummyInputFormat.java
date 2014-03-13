begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|InputFormat
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
name|InputSplit
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
name|RecordReader
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
name|Reporter
import|;
end_import

begin_comment
comment|/**  * A input format which returns one dummy key and value  */
end_comment

begin_class
DECL|class|DummyInputFormat
class|class
name|DummyInputFormat
implements|implements
name|InputFormat
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
block|{
DECL|class|EmptySplit
specifier|static
class|class
name|EmptySplit
implements|implements
name|InputSplit
block|{
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|method|getSplits (JobConf job, int numSplits)
specifier|public
name|InputSplit
index|[]
name|getSplits
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|int
name|numSplits
parameter_list|)
throws|throws
name|IOException
block|{
name|InputSplit
index|[]
name|splits
init|=
operator|new
name|InputSplit
index|[
name|numSplits
index|]
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
name|splits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|splits
index|[
name|i
index|]
operator|=
operator|new
name|EmptySplit
argument_list|()
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
DECL|method|getRecordReader (InputSplit split, JobConf job, Reporter reporter)
specifier|public
name|RecordReader
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RecordReader
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
name|boolean
name|once
init|=
literal|false
decl_stmt|;
specifier|public
name|boolean
name|next
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|once
condition|)
block|{
name|once
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Object
name|createKey
parameter_list|()
block|{
return|return
operator|new
name|Object
argument_list|()
return|;
block|}
specifier|public
name|Object
name|createValue
parameter_list|()
block|{
return|return
operator|new
name|Object
argument_list|()
return|;
block|}
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0L
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{       }
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0.0f
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

