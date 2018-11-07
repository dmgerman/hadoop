begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
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
name|scm
operator|.
name|chillmode
operator|.
name|Precheck
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
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
import|;
end_import

begin_comment
comment|/**  * SCM utility class.  */
end_comment

begin_class
DECL|class|ScmUtils
specifier|public
specifier|final
class|class
name|ScmUtils
block|{
DECL|method|ScmUtils ()
specifier|private
name|ScmUtils
parameter_list|()
block|{   }
comment|/**    * Perform all prechecks for given scm operation.    *    * @param operation    * @param preChecks prechecks to be performed    */
DECL|method|preCheck (ScmOps operation, Precheck... preChecks)
specifier|public
specifier|static
name|void
name|preCheck
parameter_list|(
name|ScmOps
name|operation
parameter_list|,
name|Precheck
modifier|...
name|preChecks
parameter_list|)
throws|throws
name|SCMException
block|{
for|for
control|(
name|Precheck
name|preCheck
range|:
name|preChecks
control|)
block|{
name|preCheck
operator|.
name|check
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

