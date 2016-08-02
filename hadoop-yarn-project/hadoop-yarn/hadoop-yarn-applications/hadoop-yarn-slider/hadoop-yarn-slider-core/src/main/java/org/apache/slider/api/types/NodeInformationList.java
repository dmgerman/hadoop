begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api.types
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|JsonSerDeser
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_class
DECL|class|NodeInformationList
specifier|public
class|class
name|NodeInformationList
extends|extends
name|ArrayList
argument_list|<
name|NodeInformation
argument_list|>
block|{
DECL|method|NodeInformationList ()
specifier|public
name|NodeInformationList
parameter_list|()
block|{   }
DECL|method|NodeInformationList (Collection<? extends NodeInformation> c)
specifier|public
name|NodeInformationList
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|NodeInformation
argument_list|>
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|NodeInformationList (int initialCapacity)
specifier|public
name|NodeInformationList
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
DECL|method|createSerializer ()
specifier|public
specifier|static
name|JsonSerDeser
argument_list|<
name|NodeInformationList
argument_list|>
name|createSerializer
parameter_list|()
block|{
return|return
operator|new
name|JsonSerDeser
argument_list|<>
argument_list|(
name|NodeInformationList
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

