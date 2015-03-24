begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_comment
comment|/**  * A default implementation of the INodeAttributesProvider  *  */
end_comment

begin_class
DECL|class|DefaultINodeAttributesProvider
specifier|public
class|class
name|DefaultINodeAttributesProvider
extends|extends
name|INodeAttributeProvider
block|{
DECL|field|DEFAULT_PROVIDER
specifier|public
specifier|static
name|INodeAttributeProvider
name|DEFAULT_PROVIDER
init|=
operator|new
name|DefaultINodeAttributesProvider
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|// NO-OP
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// NO-OP
block|}
annotation|@
name|Override
DECL|method|getAttributes (String[] pathElements, INodeAttributes inode)
specifier|public
name|INodeAttributes
name|getAttributes
parameter_list|(
name|String
index|[]
name|pathElements
parameter_list|,
name|INodeAttributes
name|inode
parameter_list|)
block|{
return|return
name|inode
return|;
block|}
block|}
end_class

end_unit

