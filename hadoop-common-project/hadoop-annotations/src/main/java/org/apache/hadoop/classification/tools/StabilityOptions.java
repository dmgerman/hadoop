begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.classification.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|javadoc
operator|.
name|DocErrorReporter
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_class
DECL|class|StabilityOptions
class|class
name|StabilityOptions
block|{
DECL|field|STABLE_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|STABLE_OPTION
init|=
literal|"-stable"
decl_stmt|;
DECL|field|EVOLVING_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|EVOLVING_OPTION
init|=
literal|"-evolving"
decl_stmt|;
DECL|field|UNSTABLE_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|UNSTABLE_OPTION
init|=
literal|"-unstable"
decl_stmt|;
DECL|method|optionLength (String option)
specifier|public
specifier|static
name|Integer
name|optionLength
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
name|opt
init|=
name|option
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|UNSTABLE_OPTION
argument_list|)
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|EVOLVING_OPTION
argument_list|)
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|STABLE_OPTION
argument_list|)
condition|)
return|return
literal|1
return|;
return|return
literal|null
return|;
block|}
DECL|method|validOptions (String[][] options, DocErrorReporter reporter)
specifier|public
specifier|static
name|void
name|validOptions
parameter_list|(
name|String
index|[]
index|[]
name|options
parameter_list|,
name|DocErrorReporter
name|reporter
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|options
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|opt
init|=
name|options
index|[
name|i
index|]
index|[
literal|0
index|]
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|UNSTABLE_OPTION
argument_list|)
condition|)
block|{
name|RootDocProcessor
operator|.
name|stability
operator|=
name|UNSTABLE_OPTION
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|EVOLVING_OPTION
argument_list|)
condition|)
block|{
name|RootDocProcessor
operator|.
name|stability
operator|=
name|EVOLVING_OPTION
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|opt
operator|.
name|equals
argument_list|(
name|STABLE_OPTION
argument_list|)
condition|)
block|{
name|RootDocProcessor
operator|.
name|stability
operator|=
name|STABLE_OPTION
expr_stmt|;
block|}
block|}
block|}
DECL|method|filterOptions (String[][] options)
specifier|public
specifier|static
name|String
index|[]
index|[]
name|filterOptions
parameter_list|(
name|String
index|[]
index|[]
name|options
parameter_list|)
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|optionsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
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
name|options
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|options
index|[
name|i
index|]
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
name|UNSTABLE_OPTION
argument_list|)
operator|&&
operator|!
name|options
index|[
name|i
index|]
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
name|EVOLVING_OPTION
argument_list|)
operator|&&
operator|!
name|options
index|[
name|i
index|]
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
name|STABLE_OPTION
argument_list|)
condition|)
block|{
name|optionsList
operator|.
name|add
argument_list|(
name|options
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
index|[]
name|filteredOptions
init|=
operator|new
name|String
index|[
name|optionsList
operator|.
name|size
argument_list|()
index|]
index|[]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
index|[]
name|option
range|:
name|optionsList
control|)
block|{
name|filteredOptions
index|[
name|i
operator|++
index|]
operator|=
name|option
expr_stmt|;
block|}
return|return
name|filteredOptions
return|;
block|}
block|}
end_class

end_unit

