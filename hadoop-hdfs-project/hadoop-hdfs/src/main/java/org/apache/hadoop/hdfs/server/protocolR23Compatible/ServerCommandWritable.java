begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocolR23Compatible
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
name|protocolR23Compatible
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * Base class for a server command.  * Issued by the name-node to notify other servers what should be done.  * Commands are defined by actions defined in respective protocols.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ServerCommandWritable
specifier|public
specifier|abstract
class|class
name|ServerCommandWritable
implements|implements
name|Writable
block|{
DECL|field|action
specifier|private
name|int
name|action
decl_stmt|;
comment|/**    * Unknown server command constructor.    * Creates a command with action 0.    */
DECL|method|ServerCommandWritable ()
specifier|public
name|ServerCommandWritable
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a command for the specified action.    * Actions are protocol specific.    * @param action    */
DECL|method|ServerCommandWritable (int action)
specifier|public
name|ServerCommandWritable
parameter_list|(
name|int
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
comment|/**    * Get server command action.    * @return action code.    */
DECL|method|getAction ()
specifier|public
name|int
name|getAction
parameter_list|()
block|{
return|return
name|this
operator|.
name|action
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
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|action
argument_list|)
expr_stmt|;
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
name|action
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

