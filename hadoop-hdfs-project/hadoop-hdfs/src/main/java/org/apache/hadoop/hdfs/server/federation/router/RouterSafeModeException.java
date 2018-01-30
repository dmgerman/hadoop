begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

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
name|server
operator|.
name|namenode
operator|.
name|NameNode
operator|.
name|OperationCategory
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
name|ipc
operator|.
name|StandbyException
import|;
end_import

begin_comment
comment|/**  * Exception that the Router throws when it is in safe mode. This extends  * {@link StandbyException} for the client to try another Router when it gets  * this exception.  */
end_comment

begin_class
DECL|class|RouterSafeModeException
specifier|public
class|class
name|RouterSafeModeException
extends|extends
name|StandbyException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|453568188334993493L
decl_stmt|;
comment|/** Identifier of the Router that generated this exception. */
DECL|field|routerId
specifier|private
specifier|final
name|String
name|routerId
decl_stmt|;
comment|/**    * Build a new Router safe mode exception.    * @param router Identifier of the Router.    * @param op Category of the operation (READ/WRITE).    */
DECL|method|RouterSafeModeException (String router, OperationCategory op)
specifier|public
name|RouterSafeModeException
parameter_list|(
name|String
name|router
parameter_list|,
name|OperationCategory
name|op
parameter_list|)
block|{
name|super
argument_list|(
literal|"Router "
operator|+
name|router
operator|+
literal|" is in safe mode and cannot handle "
operator|+
name|op
operator|+
literal|" requests."
argument_list|)
expr_stmt|;
name|this
operator|.
name|routerId
operator|=
name|router
expr_stmt|;
block|}
comment|/**    * Get the id of the Router that generated this exception.    * @return Id of the Router that generated this exception.    */
DECL|method|getRouterId ()
specifier|public
name|String
name|getRouterId
parameter_list|()
block|{
return|return
name|this
operator|.
name|routerId
return|;
block|}
block|}
end_class

end_unit

