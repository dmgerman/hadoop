begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.blockgenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|blockgenerator
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
name|io
operator|.
name|LongWritable
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * This is the MapOutputValue class. It has the blockId and the block generation  * stamp which is needed to generate the block images in the reducer.  *  * This also stores the replication of the block, but note that it does not  * serialize this value as part of its {@link Writable} interface, and does not  * consider the replication when doing equality / hash comparisons.  */
end_comment

begin_class
DECL|class|BlockInfo
specifier|public
class|class
name|BlockInfo
implements|implements
name|Writable
block|{
DECL|method|getBlockId ()
name|LongWritable
name|getBlockId
parameter_list|()
block|{
return|return
name|blockId
return|;
block|}
DECL|method|getBlockGenerationStamp ()
name|LongWritable
name|getBlockGenerationStamp
parameter_list|()
block|{
return|return
name|blockGenerationStamp
return|;
block|}
DECL|method|getSize ()
name|LongWritable
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|getReplication ()
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
DECL|field|blockId
specifier|private
name|LongWritable
name|blockId
decl_stmt|;
DECL|field|blockGenerationStamp
specifier|private
name|LongWritable
name|blockGenerationStamp
decl_stmt|;
DECL|field|size
specifier|private
name|LongWritable
name|size
decl_stmt|;
DECL|field|replication
specifier|private
specifier|transient
name|short
name|replication
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// Used via reflection
DECL|method|BlockInfo ()
specifier|private
name|BlockInfo
parameter_list|()
block|{
name|this
operator|.
name|blockId
operator|=
operator|new
name|LongWritable
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockGenerationStamp
operator|=
operator|new
name|LongWritable
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
operator|new
name|LongWritable
argument_list|()
expr_stmt|;
block|}
DECL|method|BlockInfo (long blockid, long blockgenerationstamp, long size, short replication)
name|BlockInfo
parameter_list|(
name|long
name|blockid
parameter_list|,
name|long
name|blockgenerationstamp
parameter_list|,
name|long
name|size
parameter_list|,
name|short
name|replication
parameter_list|)
block|{
name|this
operator|.
name|blockId
operator|=
operator|new
name|LongWritable
argument_list|(
name|blockid
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockGenerationStamp
operator|=
operator|new
name|LongWritable
argument_list|(
name|blockgenerationstamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
operator|new
name|LongWritable
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
block|}
DECL|method|write (DataOutput dataOutput)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|dataOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|blockId
operator|.
name|write
argument_list|(
name|dataOutput
argument_list|)
expr_stmt|;
name|blockGenerationStamp
operator|.
name|write
argument_list|(
name|dataOutput
argument_list|)
expr_stmt|;
name|size
operator|.
name|write
argument_list|(
name|dataOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput dataInput)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|dataInput
parameter_list|)
throws|throws
name|IOException
block|{
name|blockId
operator|.
name|readFields
argument_list|(
name|dataInput
argument_list|)
expr_stmt|;
name|blockGenerationStamp
operator|.
name|readFields
argument_list|(
name|dataInput
argument_list|)
expr_stmt|;
name|size
operator|.
name|readFields
argument_list|(
name|dataInput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BlockInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BlockInfo
name|blkInfo
init|=
operator|(
name|BlockInfo
operator|)
name|o
decl_stmt|;
return|return
name|blkInfo
operator|.
name|getBlockId
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|&&
name|blkInfo
operator|.
name|getBlockGenerationStamp
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getBlockGenerationStamp
argument_list|()
argument_list|)
operator|&&
name|blkInfo
operator|.
name|getSize
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|blockId
operator|.
name|hashCode
argument_list|()
operator|+
literal|357
operator|*
name|blockGenerationStamp
operator|.
name|hashCode
argument_list|()
operator|+
literal|9357
operator|*
name|size
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

