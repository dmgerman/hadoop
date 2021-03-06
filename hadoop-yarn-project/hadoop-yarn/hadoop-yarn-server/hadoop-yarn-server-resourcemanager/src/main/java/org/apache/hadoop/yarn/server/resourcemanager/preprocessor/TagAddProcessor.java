begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.preprocessor
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|preprocessor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
import|;
end_import

begin_comment
comment|/**  * This processor will add the tag to application submission context.  */
end_comment

begin_class
DECL|class|TagAddProcessor
class|class
name|TagAddProcessor
implements|implements
name|ContextProcessor
block|{
annotation|@
name|Override
DECL|method|process (String host, String value, ApplicationId applicationId, ApplicationSubmissionContext submissionContext)
specifier|public
name|void
name|process
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|value
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|ApplicationSubmissionContext
name|submissionContext
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTags
init|=
name|submissionContext
operator|.
name|getApplicationTags
argument_list|()
decl_stmt|;
if|if
condition|(
name|applicationTags
operator|==
literal|null
condition|)
block|{
name|applicationTags
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|applicationTags
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|applicationTags
argument_list|)
expr_stmt|;
block|}
name|applicationTags
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|submissionContext
operator|.
name|setApplicationTags
argument_list|(
name|applicationTags
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

