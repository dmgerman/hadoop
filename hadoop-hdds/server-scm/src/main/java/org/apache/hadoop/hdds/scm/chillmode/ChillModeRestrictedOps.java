begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.chillmode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|chillmode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ScmOps
import|;
end_import

begin_comment
comment|/**  * Operations restricted in SCM chill mode.  */
end_comment

begin_class
DECL|class|ChillModeRestrictedOps
specifier|public
specifier|final
class|class
name|ChillModeRestrictedOps
block|{
DECL|field|restrictedOps
specifier|private
specifier|static
name|EnumSet
name|restrictedOps
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ScmOps
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ChillModeRestrictedOps ()
specifier|private
name|ChillModeRestrictedOps
parameter_list|()
block|{   }
static|static
block|{
name|restrictedOps
operator|.
name|add
argument_list|(
name|ScmOps
operator|.
name|allocateBlock
argument_list|)
expr_stmt|;
name|restrictedOps
operator|.
name|add
argument_list|(
name|ScmOps
operator|.
name|allocateContainer
argument_list|)
expr_stmt|;
block|}
DECL|method|isRestrictedInChillMode (ScmOps opName)
specifier|public
specifier|static
name|boolean
name|isRestrictedInChillMode
parameter_list|(
name|ScmOps
name|opName
parameter_list|)
block|{
return|return
name|restrictedOps
operator|.
name|contains
argument_list|(
name|opName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

