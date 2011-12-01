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
name|io
operator|.
name|ObjectWritable
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/**  * Response to {@link DatanodeProtocol#sendHeartbeat}  */
DECL|class|HeartbeatResponse
specifier|public
class|class
name|HeartbeatResponse
implements|implements
name|Writable
block|{
comment|/** Commands returned from the namenode to the datanode */
DECL|field|commands
specifier|private
name|DatanodeCommand
index|[]
name|commands
decl_stmt|;
DECL|method|HeartbeatResponse ()
specifier|public
name|HeartbeatResponse
parameter_list|()
block|{
comment|// Empty constructor required for Writable
block|}
DECL|method|HeartbeatResponse (DatanodeCommand[] cmds)
specifier|public
name|HeartbeatResponse
parameter_list|(
name|DatanodeCommand
index|[]
name|cmds
parameter_list|)
block|{
name|commands
operator|=
name|cmds
expr_stmt|;
block|}
DECL|method|getCommands ()
specifier|public
name|DatanodeCommand
index|[]
name|getCommands
parameter_list|()
block|{
return|return
name|commands
return|;
block|}
comment|///////////////////////////////////////////
comment|// Writable
comment|///////////////////////////////////////////
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
name|int
name|length
init|=
name|commands
operator|==
literal|null
condition|?
literal|0
else|:
name|commands
operator|.
name|length
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectWritable
operator|.
name|writeObject
argument_list|(
name|out
argument_list|,
name|commands
index|[
name|i
index|]
argument_list|,
name|commands
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
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
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|commands
operator|=
operator|new
name|DatanodeCommand
index|[
name|length
index|]
expr_stmt|;
name|ObjectWritable
name|objectWritable
init|=
operator|new
name|ObjectWritable
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|commands
index|[
name|i
index|]
operator|=
operator|(
name|DatanodeCommand
operator|)
name|ObjectWritable
operator|.
name|readObject
argument_list|(
name|in
argument_list|,
name|objectWritable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

