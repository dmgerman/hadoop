begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|util
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
name|cblock
operator|.
name|meta
operator|.
name|ContainerDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * NOTE : This class is only for testing purpose.  *  * Mock an underlying container storage layer, expose to CBlock to perform  * IO. While in this mock implementation, a container is nothing more than  * a in memory hashmap.  *  * This is to allow volume creation call and perform standalone tests.  */
end_comment

begin_class
DECL|class|ContainerLookUpService
specifier|public
specifier|final
class|class
name|ContainerLookUpService
block|{
specifier|private
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|ContainerDescriptor
argument_list|>
DECL|field|containers
name|containers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Return an *existing* container with given Id.    *    * TODO : for testing purpose, return a new container if the given Id    * is not found    *    * found    * @param containerID    * @return    */
DECL|method|lookUp (String containerID)
specifier|public
specifier|static
name|ContainerDescriptor
name|lookUp
parameter_list|(
name|String
name|containerID
parameter_list|)
block|{
if|if
condition|(
operator|!
name|containers
operator|.
name|containsKey
argument_list|(
name|containerID
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"A container id never seen, return a new one "
operator|+
literal|"for testing purpose:"
operator|+
name|containerID
argument_list|)
expr_stmt|;
name|containers
operator|.
name|put
argument_list|(
name|containerID
argument_list|,
operator|new
name|ContainerDescriptor
argument_list|(
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containers
operator|.
name|get
argument_list|(
name|containerID
argument_list|)
return|;
block|}
DECL|method|addContainer (String containerID)
specifier|public
specifier|static
name|void
name|addContainer
parameter_list|(
name|String
name|containerID
parameter_list|)
block|{
name|containers
operator|.
name|put
argument_list|(
name|containerID
argument_list|,
operator|new
name|ContainerDescriptor
argument_list|(
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerLookUpService ()
specifier|private
name|ContainerLookUpService
parameter_list|()
block|{    }
block|}
end_class

end_unit

