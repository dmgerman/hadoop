begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|io
operator|.
name|Writable
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
name|WritableUtils
import|;
end_import

begin_comment
comment|/**  * A data structure to store the blocks in an incremental block report.   */
end_comment

begin_class
DECL|class|ReceivedDeletedBlockInfo
specifier|public
class|class
name|ReceivedDeletedBlockInfo
implements|implements
name|Writable
block|{
DECL|field|block
name|Block
name|block
decl_stmt|;
DECL|field|status
name|BlockStatus
name|status
decl_stmt|;
DECL|field|delHints
name|String
name|delHints
decl_stmt|;
DECL|enum|BlockStatus
specifier|public
specifier|static
enum|enum
name|BlockStatus
block|{
DECL|enumConstant|RECEIVING_BLOCK
name|RECEIVING_BLOCK
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|RECEIVED_BLOCK
name|RECEIVED_BLOCK
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|DELETED_BLOCK
name|DELETED_BLOCK
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|code
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
DECL|method|BlockStatus (int code)
name|BlockStatus
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|method|getCode ()
specifier|public
name|int
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|fromCode (int code)
specifier|public
specifier|static
name|BlockStatus
name|fromCode
parameter_list|(
name|int
name|code
parameter_list|)
block|{
for|for
control|(
name|BlockStatus
name|bs
range|:
name|BlockStatus
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|bs
operator|.
name|code
operator|==
name|code
condition|)
block|{
return|return
name|bs
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|method|ReceivedDeletedBlockInfo ()
specifier|public
name|ReceivedDeletedBlockInfo
parameter_list|()
block|{   }
DECL|method|ReceivedDeletedBlockInfo ( Block blk, BlockStatus status, String delHints)
specifier|public
name|ReceivedDeletedBlockInfo
parameter_list|(
name|Block
name|blk
parameter_list|,
name|BlockStatus
name|status
parameter_list|,
name|String
name|delHints
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|blk
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|delHints
operator|=
name|delHints
expr_stmt|;
block|}
DECL|method|getBlock ()
specifier|public
name|Block
name|getBlock
parameter_list|()
block|{
return|return
name|this
operator|.
name|block
return|;
block|}
DECL|method|setBlock (Block blk)
specifier|public
name|void
name|setBlock
parameter_list|(
name|Block
name|blk
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|blk
expr_stmt|;
block|}
DECL|method|getDelHints ()
specifier|public
name|String
name|getDelHints
parameter_list|()
block|{
return|return
name|this
operator|.
name|delHints
return|;
block|}
DECL|method|setDelHints (String hints)
specifier|public
name|void
name|setDelHints
parameter_list|(
name|String
name|hints
parameter_list|)
block|{
name|this
operator|.
name|delHints
operator|=
name|hints
expr_stmt|;
block|}
DECL|method|getStatus ()
specifier|public
name|BlockStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
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
name|ReceivedDeletedBlockInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReceivedDeletedBlockInfo
name|other
init|=
operator|(
name|ReceivedDeletedBlockInfo
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|block
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getBlock
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|status
operator|==
name|other
operator|.
name|status
operator|&&
operator|(
name|this
operator|.
name|delHints
operator|==
name|other
operator|.
name|delHints
operator|||
name|this
operator|.
name|delHints
operator|!=
literal|null
operator|&&
name|this
operator|.
name|delHints
operator|.
name|equals
argument_list|(
name|other
operator|.
name|delHints
argument_list|)
operator|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
assert|assert
literal|false
operator|:
literal|"hashCode not designed"
assert|;
return|return
literal|0
return|;
block|}
DECL|method|blockEquals (Block b)
specifier|public
name|boolean
name|blockEquals
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
return|return
name|this
operator|.
name|block
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|isDeletedBlock ()
specifier|public
name|boolean
name|isDeletedBlock
parameter_list|()
block|{
return|return
name|status
operator|==
name|BlockStatus
operator|.
name|DELETED_BLOCK
return|;
block|}
annotation|@
name|Override
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
block|{
name|this
operator|.
name|block
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|this
operator|.
name|status
operator|.
name|code
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|status
operator|==
name|BlockStatus
operator|.
name|DELETED_BLOCK
condition|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|this
operator|.
name|delHints
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
block|{
name|this
operator|.
name|block
operator|=
operator|new
name|Block
argument_list|()
expr_stmt|;
name|this
operator|.
name|block
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|BlockStatus
operator|.
name|fromCode
argument_list|(
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|status
operator|==
name|BlockStatus
operator|.
name|DELETED_BLOCK
condition|)
block|{
name|this
operator|.
name|delHints
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|block
operator|.
name|toString
argument_list|()
operator|+
literal|", status: "
operator|+
name|status
operator|+
literal|", delHint: "
operator|+
name|delHints
return|;
block|}
block|}
end_class

end_unit

